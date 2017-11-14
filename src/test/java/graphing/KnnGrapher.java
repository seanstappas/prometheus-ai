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
    private static final String ANIMAL_DATA_PATH = "data/animalData";

    public static void main(String args[]) {
        // Create KNN
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        KnowledgeNodeNetwork knn = prometheus.getKnowledgeNodeNetwork();
        List<KnowledgeNode> knowledgeNodes = new ArrayList<>();
        KnnDataLoader.loadDate(knn, ANIMAL_DATA_PATH, knowledgeNodes);

        // Activate random Tags
        Collections.shuffle(knowledgeNodes);
        Set<String> activeIDs = new HashSet<>();
        for (int i = 0; i < knowledgeNodes.size() / 2; i++) {
            Tag inputTag = knowledgeNodes.get(i).inputTag;
            knn.addFiredTag(inputTag);
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
            String id = knowledgeNode.inputTag.toString();
            for (Tag t : knowledgeNode.outputTags) {
                String tagID = t.toString();
                graph.addEdge(id + tagID, id, tagID);
            }
        }

        for (Node node : graph) {
            String id = node.getId();
            node.addAttribute("ui.label", id);
            if (activeIDs.contains(id)) {
                node.setAttribute("ui.class", "marked");
            }
        }
    }
}
