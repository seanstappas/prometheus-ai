package graphing;

import com.google.inject.Guice;
import integration.KnnDataLoader;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.graphstream.graph.Node;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Tag;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

abstract class KnnGraphVisualizer extends GraphVisualizer {
    private static final int NUM_INITIAL_ACTIVE_TAGS = 5;
    private static final String KNN_STYLE_SHEET_PATH = "graphs/knn_graph_style_sheet.css";
    private static final String ANIMAL_DATA_PATH = "data/animalData2.txt";
    private Set<String> activeIDs;

    KnowledgeNodeNetwork knn;

    abstract Set<Tag> search();

    Set<Tag> getInitialActiveTags() {
        Set<Tag> activeTags = new HashSet<>();
        int i = 0;
        for (KnowledgeNode kn : knn.getKnowledgeNodes()) {
            activeTags.add(kn.getInputTag());
            if (i == NUM_INITIAL_ACTIVE_TAGS) {
                break;
            }
            i++;
        }
        return activeTags;
    }

    @Override
    String getStyleSheetPath() {
        return KNN_STYLE_SHEET_PATH;
    }

    @Override
    int getSleepDelay() {
        return 1000;
    }

    @Override
    void setupNodes() {
        loadKNN();
        addInitialNodes();
        activateInitialNodes();
    }

    private void loadKNN() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
        KnnDataLoader.loadData(knn, ANIMAL_DATA_PATH);
        activeIDs = new HashSet<>();
    }

    private void addInitialNodes() {
        for (KnowledgeNode knowledgeNode : knn.getKnowledgeNodes()) {
            String inputID = knowledgeNode.getInputTag().toString();
            String knID = knowledgeNode.toString();
            graph.addEdge(inputID + knID, inputID, knID, true);
            updateNodeTagClass(inputID);
            updateNodeTagClass(knID);
            for (Tag t : knowledgeNode.getOutputTags()) {
                String outputID = t.toString();
                graph.addEdge(knID + outputID, knID, outputID, true);
                updateNodeTagClass(outputID);
            }
        }
    }

    private void activateInitialNodes() {
        for (Tag t : getInitialActiveTags()) {
            knn.addActiveTag(t);
            String id = t.toString();
            activeIDs.add(id);
            updateNodeTagClass(id);
        }
    }

    @Override
    boolean updateGraph() {
        Set<Tag> activatedTags = search();
        for (Tag t : knn.getActiveTags()) {
            String id = t.toString();
            activeIDs.add(id);
            updateNodeTagClass(id);

            KnowledgeNode kn = knn.getKnowledgeNode(t);
            if (kn.isFired()) {
                String knID = kn.toString();
                activeIDs.add(knID);
                updateNodeTagClass(knID);
            }
        }
        return !activatedTags.isEmpty();
    }

    @Override
    public void buttonPushed(String id) {
        Node node = graph.getNode(id);
        node.setAttribute("ui.class", "clicked");
    }

    @Override
    public void buttonReleased(String id) {
        updateNodeTagClass(id);
    }

    private void updateNodeTagClass(String id) {
        Node node = graph.getNode(id);
        node.addAttribute("ui.label", id);
        String nodeType = id.split("\\[")[0].toLowerCase();
        if (activeIDs.contains(id)) {
            node.setAttribute("ui.class", MessageFormat.format("active_{0}", nodeType));
        } else {
            node.setAttribute("ui.class", nodeType);
        }
    }
}
