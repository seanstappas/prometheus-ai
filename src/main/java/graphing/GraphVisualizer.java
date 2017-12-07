package graphing;

import java.io.IOException;
import java.text.MessageFormat;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkSVG2;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

/**
 * Graph visualizer with multiple abstract methods.
 */
abstract class GraphVisualizer implements ViewerListener {
    private Graph graph;
    private boolean updated = false;
    private ViewerPipe fromViewer;
    private FileSinkSVG2 svgSink;
    private boolean loop = true;

    /**
     * @return the GraphStream graph object
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Sets the graph to be updated.
     *
     * @param updated if the graph should be updated.
     */
    public void setUpdated(final boolean updated) {
        this.updated = updated;
    }

    /**
     * @return the path to the CSS stylesheet of the graph
     */
    abstract String getStyleSheetPath();

    /**
     * @param suffix the suffix to apply to the screenshot path
     * @return the screenshot path
     */
    abstract String getScreenshotSavePath(String suffix);

    /**
     * @return the sleep delay between graph updates
     */
    abstract int getSleepDelay();

    /**
     * Sets up the graph nodes.
     */
    abstract void setupNodes();

    /**
     * @return if the graph was changed
     */
    abstract boolean updateGraph();

    @Override
    public abstract void buttonPushed(String id);

    @Override
    public abstract void buttonReleased(String id);

    /**
     * Visualize the graph.
     *
     * @param saveScreenshot true if the graph should be saved to screenshot at
     *                       every iteration
     * @param display        true if the visualization should display in a new
     *                       window
     */
    final void visualize(final boolean saveScreenshot, final boolean display) {
        setupGraph(display);
        setupNodes();
        int iteration = 0;
        long lastTime = System.currentTimeMillis();
        while (loop) {
            if (display) {
                fromViewer.pump();
            }
            if (updated) {
                if (System.currentTimeMillis() - lastTime >= getSleepDelay()) {
                    iteration++;
                    if (saveScreenshot && iteration == 1) {
                        saveScreenshot("0");
                    }
                    updated = updateGraph();
                    if (updated && saveScreenshot) {
                        saveScreenshot(String.valueOf(iteration));
                    }
                    lastTime = System.currentTimeMillis();
                    if (!display) {
                        loop = false;
                    }
                }
            }
        }
    }

    /**
     * Sets up the graph.
     *
     * @param display true if the visualization should display in a new window
     */
    private void setupGraph(final boolean display) {
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new SingleGraph("graph");
        graph.addAttribute("ui.stylesheet",
                MessageFormat.format("url({0})", getStyleSheetPath()));
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        if (display) {
            final Viewer viewer = graph.display();
            fromViewer = viewer.newViewerPipe();
            fromViewer.addViewerListener(this);
            fromViewer.addSink(graph);
        }
        svgSink = new FileSinkSVG2();
    }

    /**
     * Saves the graph to a screenshot.
     *
     * @param suffix the suffix to apply to the screenshot.
     */
    void saveScreenshot(final String suffix) {
        try {
            svgSink.writeAll(graph, MessageFormat
                    .format("{0}.svg", getScreenshotSavePath(suffix)));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        graph.addAttribute("ui.screenshot",
                MessageFormat.format("{0}.png", getScreenshotSavePath(suffix)));
    }

    @Override
    public final void viewClosed(final String viewName) {
        loop = false;
    }
}
