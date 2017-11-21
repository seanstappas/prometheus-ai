package graphing;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import java.text.MessageFormat;

abstract class GraphVisualizer implements ViewerListener {
    Graph graph;
    ViewerPipe fromViewer;

    private boolean loop = true;

    abstract String getStyleSheetPath();

    abstract String getScreenshotSavePath(String suffix);

    abstract int getSleepDelay();

    abstract void setupNodes();

    abstract boolean updateGraph();

    @Override
    public abstract void buttonPushed(String id);

    @Override
    public abstract void buttonReleased(String id);

    final void visualize() throws InterruptedException {
        setupGraph();
        setupNodes();
        int iteration = 0;
        Thread.sleep(2000);
        saveScreenshot(String.valueOf(iteration));
        long lastTime = System.currentTimeMillis();
        while (loop) {
            fromViewer.pump();
            if (System.currentTimeMillis() - lastTime >= getSleepDelay()) {
                iteration++;
                boolean updated = updateGraph();
                if (updated) {
                    saveScreenshot(String.valueOf(iteration));
                }
                lastTime = System.currentTimeMillis();
            }
        }
    }

    private void setupGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new SingleGraph("graph");
        graph.addAttribute("ui.stylesheet", MessageFormat.format("url({0})", getStyleSheetPath()));
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        Viewer viewer = graph.display();
        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);
    }

    private void saveScreenshot(String suffix) {
        graph.addAttribute("ui.screenshot", getScreenshotSavePath(suffix));
    }

    @Override
    public final void viewClosed(String viewName) {
        loop = false;
    }
}
