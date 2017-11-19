package graphing;

import com.google.inject.Guice;
import integration.KnnDataLoader;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Tag;

import java.util.*;

public class KnnGrapher {
    private static final String ANIMAL_DATA_PATH = "data/animalData.txt";

    public static void main(String args[]) throws InterruptedException {
        // Create KNN
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        KnowledgeNodeNetwork knn = prometheus.getKnowledgeNodeNetwork();
        List<KnowledgeNode> knowledgeNodes = new ArrayList<>();
        KnnDataLoader.loadData(knn, ANIMAL_DATA_PATH, knowledgeNodes);

        // Activate random Tags
        Collections.shuffle(knowledgeNodes);
        Set<String> activeIDs = new HashSet<>();
        for (int i = 0; i < knowledgeNodes.size() / 2; i++) {
            Tag inputTag = knowledgeNodes.get(i).getInputTag();
            knn.addActiveTag(inputTag);
            activeIDs.add(inputTag.toString());
        }


        Graph graph = new SingleGraph("tutorial 1");

        String styleSheet = "node {" +
                "	fill-color: black;" +
                "}" +
                "node.marked {" +
                "	fill-color: red;" +
                "}";
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.display();

        for (KnowledgeNode knowledgeNode : knowledgeNodes) {
            String inputID = knowledgeNode.getInputTag().toString();
            for (Tag t : knowledgeNode.getOutputTags()) {
                String outputID = t.toString();
                graph.addEdge(inputID + outputID, inputID, outputID, true);
                updateLabels(activeIDs, graph);
                Thread.sleep(100);
            }
        }

    }

    private static void updateLabels(Set<String> activeIDs, Graph graph) {
        for (Node node : graph) {
            String id = node.getId();
            node.addAttribute("ui.label", id);
            if (activeIDs.contains(id)) {
                node.setAttribute("ui.class", "marked");
            }
        }
    }
}
