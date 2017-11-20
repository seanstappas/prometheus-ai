package graphing;

import com.google.inject.Guice;
import integration.KnnDataLoader;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Tag;

import java.util.*;

public class KnnGrapher implements ViewerListener {
    private Graph graph;
    private Set<String> activeIDs;

    private static final String ANIMAL_DATA_PATH = "data/animalData.txt";

    public static void main(String args[]) throws InterruptedException {
        new KnnGrapher();
    }

    private KnnGrapher() throws InterruptedException {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // Create KNN
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        KnowledgeNodeNetwork knn = prometheus.getKnowledgeNodeNetwork();
        List<KnowledgeNode> knowledgeNodes = new ArrayList<>();
        KnnDataLoader.loadData(knn, ANIMAL_DATA_PATH, knowledgeNodes);

        // Activate random Tags
        Collections.shuffle(knowledgeNodes);
        activeIDs = new HashSet<>();
        for (int i = 0; i < knowledgeNodes.size() / 2; i++) {
            Tag inputTag = knowledgeNodes.get(i).getInputTag();
            knn.addActiveTag(inputTag);
            activeIDs.add(inputTag.toString());
        }


        graph = new SingleGraph("tutorial 1");
        graph.addAttribute("ui.stylesheet", "url('data/graph_style_sheet.txt')");
        graph.setAutoCreate(true);
        graph.setStrict(false);graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        Viewer viewer = graph.display();

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        for (KnowledgeNode knowledgeNode : knowledgeNodes) {
            String inputID = knowledgeNode.getInputTag().toString();
            String knID = knowledgeNode.toString();
            graph.addEdge(inputID + knID, inputID, knID, true);

            for (Tag t : knowledgeNode.getOutputTags()) {
                String outputID = t.toString();
//                graph.addEdge(inputID + outputID, inputID, outputID, true);
                graph.addEdge(knID + outputID, knID, outputID, true);
                updateLabels();
                Thread.sleep(100);
            }
        }

        while (true) fromViewer.pump();

    }

    private void updateLabels() {
        for (Node node : graph) {
            updateNode(node);
        }
    }

    private void updateNode(Node node) {
        String id = node.getId();
        String nodeType = id.split("\\[")[0].toLowerCase();
        node.addAttribute("ui.label", id);
        if (activeIDs.contains(id)) {
            node.setAttribute("ui.class", "active");
        } else {
            node.setAttribute("ui.class", nodeType);
        }
    }

    @Override
    public void viewClosed(String viewName) {

    }

    @Override
    public void buttonPushed(String id) {
        System.out.println("Button pushed on node " + id);
        Node node = graph.getNode(id);
        node.setAttribute("ui.class", "clicked");
    }

    @Override
    public void buttonReleased(String id) {
        System.out.println("Button released on node "+id);
        Node node = graph.getNode(id);
        updateNode(node);
    }
}
