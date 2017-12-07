package graphing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.graphstream.graph.Node;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

/**
 * Visualizer for the KNN.
 */
abstract class KnnGraphVisualizer extends GraphVisualizer {
    private static final int SLEEP_DELAY = 1500;
    private static final int NUM_INITIAL_ACTIVE_TAGS = 5;
    private static final String KNN_STYLE_SHEET_PATH =
            "graphs/knn/knn_graph_style_sheet.css";
    private static final String ANIMAL_DATA_PATH = "data/animalData2.txt";
    private static final String RESET_ID = "reset";
    private static final String START_ID = "start";
    private static final String STOP_ID = "stop";
    private static final String TOGGLE_LABELS_ID = "toggle_labels";
    private static final double START_BUTTON_Y = 0.5;
    private static final double TOGGLE_BUTTON_Y = 1.5;
    private final Set<String> factIDs = new HashSet<>();
    private final Set<String> recommendationIDs = new HashSet<>();
    private final Set<String> ruleIDs = new HashSet<>();
    private final Set<String> knIDs = new HashSet<>();
    private boolean forceContinue = false;
    private KnowledgeNodeNetwork knn;
    private Set<String> activeIDs;
    private boolean showLabels = true;
    private int sleepDelay = SLEEP_DELAY;

    /**
     * Searches in the KNN.
     *
     * @return the Tags activated as a result of searching.
     */
    abstract Set<Tag> search();

    @Override
    String getStyleSheetPath() {
        return KNN_STYLE_SHEET_PATH;
    }

    @Override
    int getSleepDelay() {
        return sleepDelay;
    }

    /**
     * Sets the sleep delay.
     *
     * @param sleepDelay the sleep delay between graph updates.
     */
    void setSleepDelay(final int sleepDelay) {
        this.sleepDelay = sleepDelay;
    }

    @Override
    void setupNodes() {
        loadKNN();
        addButtons();
        addInitialNodes();
        activateInitialNodes();
    }

    @Override
    boolean updateGraph() {
        final Set<Tag> activatedTags = search();
        for (final Tag t : knn.getActiveTags()) {
            final String id = t.toString();
            activeIDs.add(id);
            updateNodeTagClass(id);

            final KnowledgeNode kn = knn.getKnowledgeNode(t);
            if (kn != null && kn.isFired()) {
                final String knID = kn.toString();
                activeIDs.add(knID);
                updateNodeTagClass(knID);
            }
        }
        return !activatedTags.isEmpty() || forceContinue;
    }

    @Override
    public void buttonPushed(final String id) {
        final Node node = getGraph().getNode(id);
        if (RESET_ID.equals(id)) {
            node.setAttribute("ui.class", "butt_clicked");
            knn.clearActiveTags();
            final Set<String> prevActiveIDs = new HashSet<>(activeIDs);
            activeIDs.clear();
            for (final String activeID : prevActiveIDs) {
                updateNodeTagClass(activeID);
            }
            loadKNN();
            activateInitialNodes();
        } else if (START_ID.equals(id)) {
            node.setAttribute("ui.class", "butt_clicked");
            getGraph().getNode(STOP_ID).setAttribute("ui.class", "butt");
            setUpdated(true);
        } else if (STOP_ID.equals(id)) {
            node.setAttribute("ui.class", "butt_clicked");
            getGraph().getNode(START_ID).setAttribute("ui.class", "butt");
            setUpdated(false);
        } else if (TOGGLE_LABELS_ID.equals(id)) {
            if (showLabels) {
                node.setAttribute("ui.class", "butt_clicked");
            } else {
                node.setAttribute("ui.class", "butt");
            }
            showLabels = !showLabels;
            for (final Node n : getGraph().getEachNode()) {
                updateNodeTagClass(n.getId());
            }
        } else {
            node.setAttribute("ui.class", "clicked");
        }
    }

    @Override
    public void buttonReleased(final String id) {
        updateNodeTagClass(id);
    }

    /**
     * Gets the KNN to be visualized.
     *
     * @return the KNN to be visualized
     */
    public KnowledgeNodeNetwork getKnn() {
        return knn;
    }

    /**
     * Sets if the visualization should be forced to continue to the next
     * iteration.
     *
     * @param forceContinue true if the visualization should be forced
     */
    public void setForceContinue(final boolean forceContinue) {
        this.forceContinue = forceContinue;
    }

    /**
     * @return the initial active Tags in the KNN
     */
    Set<Tag> getInitialActiveTags() {
        final Set<Tag> activeTags = new HashSet<>();
        int i = 0;
        final ArrayList<KnowledgeNode> lstKNs =
                new ArrayList<>(knn.getKnowledgeNodes());
        Collections.shuffle(lstKNs);
        for (final KnowledgeNode kn : lstKNs) {
            activeTags.add(kn.getInputTag());
            if (i == NUM_INITIAL_ACTIVE_TAGS) {
                break;
            }
            i++;
        }
        return activeTags;
    }

    /**
     * @return the data path to read KNN data from.
     */
    String getKnnDataPath() {
        return ANIMAL_DATA_PATH;
    }

    /**
     * Adds buttons to the visualizer output.
     */
    private void addButtons() {
        addButton(RESET_ID, "Reset", 0, 0, false);
        addButton(START_ID, "Start", 0, START_BUTTON_Y, false);
        addButton(STOP_ID, "Stop", 0, 1, true);
        addButton(TOGGLE_LABELS_ID, "Labels", 0, TOGGLE_BUTTON_Y, false);
    }

    /**
     * Adds a certain button to the screen.
     *
     * @param id      the ID of the button
     * @param label   the label of the button
     * @param x       the x position of the button
     * @param y       the y position of the button
     * @param clicked if the button is initially clicked
     */
    private void addButton(final String id, final String label, final double x,
                           final double y,
                           final boolean clicked) {
        final Node node = getGraph().addNode(id);
        node.addAttribute("layout.frozen");
        node.addAttribute("xy", x, y);
        if (clicked) {
            node.setAttribute("ui.class", "butt_clicked");
        } else {
            node.setAttribute("ui.class", "butt");
        }
        node.setAttribute("ui.label", label);
    }

    /**
     * Loads the KNN from file.
     */
    private void loadKNN() {
        final Prometheus prometheus =
                Guice.createInjector(new PrometheusModule())
                        .getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
        knn.loadData(getKnnDataPath());
        activeIDs = new HashSet<>();
    }

    /**
     * Adds the initial KNN nodoes.
     */
    private void addInitialNodes() {
        for (final KnowledgeNode knowledgeNode : knn.getKnowledgeNodes()) {
            final Tag inputTag = knowledgeNode.getInputTag();
            final String inputID = inputTag.toString();
            addTagID(inputTag, inputID);
            final String knID = knowledgeNode.toString();
            knIDs.add(knID);
            getGraph().addEdge(inputID + knID, inputID, knID, true);
            updateNodeTagClass(inputID);
            updateNodeTagClass(knID);
            for (final Tag t : knowledgeNode.getOutputTags()) {
                final String outputID = t.toString();
                addTagID(t, outputID);
                getGraph().addEdge(knID + outputID, knID, outputID, true);
                updateNodeTagClass(outputID);
            }
        }
    }

    /**
     * Add a certain Tag to the KNN.
     *
     * @param inputTag the tag to add
     * @param inputID  the ID of the tag
     */
    private void addTagID(final Tag inputTag, final String inputID) {
        if (inputTag instanceof Fact) {
            factIDs.add(inputID);
        } else if (inputTag instanceof Recommendation) {
            recommendationIDs.add(inputID);
        } else if (inputTag instanceof Rule) {
            ruleIDs.add(inputID);
        }
    }

    /**
     * Activate the initial nodes in the KNN.
     */
    private void activateInitialNodes() {
        for (final Tag t : getInitialActiveTags()) {
            knn.addActiveTag(t);
            final String id = t.toString();
            activeIDs.add(id);
            updateNodeTagClass(id);
        }
    }

    /**
     * Update the node specified by the given ID.
     *
     * @param id the ID of the node
     */
    private void updateNodeTagClass(final String id) {
        final Node node = getGraph().getNode(id);
        if (node != null) {
            if (RESET_ID.equals(id)) {
                node.setAttribute("ui.class", "butt");
            } else if (!START_ID.equals(id) && !STOP_ID.equals(id)
                    && !TOGGLE_LABELS_ID.equals(id)) {
                if (showLabels) {
                    node.setAttribute("ui.label", id);
                } else {
                    node.setAttribute("ui.label", "");
                }
                String nodeType = "";
                if (factIDs.contains(id)) {
                    nodeType = "fact";
                } else if (recommendationIDs.contains(id)) {
                    nodeType = "recommendation";
                } else if (ruleIDs.contains(id)) {
                    nodeType = "rule";
                } else if (knIDs.contains(id)) {
                    nodeType = "knowledgenode";
                }
                if (activeIDs.contains(id)) {
                    node.setAttribute("ui.class",
                            MessageFormat.format("active_{0}", nodeType));
                } else {
                    node.setAttribute("ui.class", nodeType);
                }
            }
        }
    }
}
