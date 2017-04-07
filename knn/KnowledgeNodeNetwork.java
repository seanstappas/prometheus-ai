package knn;

import interfaces.PrometheusLayer;
import tags.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Knowledge Node Network (KNN)
 */
public class KnowledgeNodeNetwork implements PrometheusLayer {
    // TODO Allow cyclic graph (recursion)
    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;

    // TODO: Add Sigmoid cache + sigmoid function (perhaps in outside class)

    /**
     * Creates a new Knowledge Node Network (KNN) based on a database.
     *
     * @param dbFilename  the filename of the database to be read from (probably CSV or JSON)
     */
    public KnowledgeNodeNetwork(String dbFilename) {
        mapKN = new HashMap<>();
        activeTags = new HashSet<>();
    }

    /**
     * Resets the KNN to a state from a database.
     *
     * @param dbFilename  the filename of the database to be read from
     */
    public void reset(String dbFilename) {

    }

    /**
     * Resets the KNN by clearing all data structures.
     */
    public void resetEmpty() {
        clearKN();
    }

    /**
     * Saves the current state of the KNN to a database.
     *
     * @param dbFilename  the filename of the database
     */
    public void saveKNN(String dbFilename) {

    }

    /**
     * Clears all the Knowledge Nodes from the KNN.
     */
    public void clearKN() {
        mapKN.clear();
        activeTags.clear();
    }

    /**
     * Adds a Knowledge Node to the KNN.
     *
     * @param kn  the Knowledge Node to be added
     */
    public void addKN(KnowledgeNode kn) {
        mapKN.put(kn.inputTag, kn);
    }

    /**
     * Deletes a Knowledge Node from the KNN.
     *
     * @param tag  the input Tag of the Knowledge Node to be deleted
     */
    public void delKN(Tag tag) {
        mapKN.remove(tag);
    }

    /**
     * Adds a fired Tag to the KNN.
     *
     * @param tag  the fired Tag to be added
     */
    public void addFiredTag(Tag tag) {
        activeTags.add(tag);
    }

    public Set<Tag> getActiveTags() {
        return activeTags;
    }


    /**
     * Makes the KNN think, and start cascaded activation and firing if possible. Chooses either thinkForwards(),
     * thinkBackwards() or thinkLambda() internally based on a command from the META (activeTagsMETA).
     * TODO: If no recommendations are fired, think() will resort to either thinkBackwards or thinkLambda. (Not theoretically well understood)
     * TODO: People generally thinkBackwards all the time in the background. In the future, could have a background thread that thinks backwards...
     *
     * @return  the Tags activated as a result of thinking
     */
    public Set<Tag> think() {
        return thinkForwards();
    }

    /**
     * Thinks for a fixed number of cycles. The number of cycles represents how much effort is being put into thinking.
     * Each cycle is a run-through of all the fired Tags, activating and firing new Tags if possible. Note that a Tag
     * that becomes active in a cycle is not iterated over in that same cycle, and must wait until the next cycle to
     * cascade further activation.
     *
     * @param numberOfCycles  the number of cycles to think for
     * @return                the Tags activated as a result of thinking
     */
    public Set<Tag> think(int numberOfCycles) {
        return thinkForwards(numberOfCycles);
    }

    /**
     * Thinking backwards works as follows: given output tags, the system attempts to find the associated input Tags
     * with some degree of confidence.
     */
    private void thinkBackwards() {
        Set<Tag> pendingFacts = new HashSet<>();
        do {
            pendingFacts.clear();
            for (KnowledgeNode kn : mapKN.values()) {
                boolean inputActivated = true;
                for (Tag t : kn.outputTags) {
                    if (!activeTags.contains(t)) { // Currently only activates input if ALL output Tags are true
                        inputActivated = false;
                        break;
                    }
                }
                if (inputActivated)
                    pendingFacts.add(kn.inputTag);
            }
            activeTags.addAll(pendingFacts);
        } while (!pendingFacts.isEmpty());
    }

    /**
     * Combination of thinkBackwards and thinkForwards. First the networks works backwards, then moves forward to
     * determine the correct memory.
     * TODO?: How far backwards do you go before going forwards?
     * TODO?: Specific use case for lambda thinking?
     */
    private void thinkLambda() {
        thinkBackwards();
        thinkForwards();
    }

    /**
     * Makes the ES think forwards using simple forward activation of knowledge nodes. If input tag is activated and
     * activation is greater than the threshold, the output tags are activated, and this is cascaded through the
     * network.
     *
     * @return  the Tags activated as a result of thinking
     */
    private Set<Tag> thinkForwards() {
        Set<Tag> totalActivatedTags = new HashSet<>();
        Set<Tag> activatedTags;
        do {
            activatedTags = forwardThinkCycle();
            totalActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        return totalActivatedTags;
    }

    /**
     * Makes the ES think forwards for a fixed number of cycles.
     *
     * @param numberOfCycles  the number of cycles to think for
     * @return                the Tags activated as a result of thinking
     */
    private Set<Tag> thinkForwards(int numberOfCycles) {
        Set<Tag> totalActivatedTags = new HashSet<>();
        for (int i = 0; i < numberOfCycles; i++) {
            Set<Tag> activatedTags = forwardThinkCycle();
            if (activatedTags.isEmpty()) {
                break;
            }
            totalActivatedTags.addAll(activatedTags);
        }
        return totalActivatedTags;
    }

    /**
     * Makes the ES think forwards for a single cycle.
     *
     * @return  the Tags activated as a result of thinking
     */
    private Set<Tag> forwardThinkCycle() {
        Set<Tag> pendingActiveTags = new HashSet<>();
        for (Tag t : activeTags) {
            if (mapKN.containsKey(t)) {
                pendingActiveTags.addAll(excite(mapKN.get(t)));
            }
        }
        activeTags.addAll(pendingActiveTags);
        return pendingActiveTags;
    }

    /**
     * Excites a Knowledge Node. If excitation leads to firing, this will return the output Tags fired.
     *
     * @param knowledgeNode  the Knowledge Node to excite
     * @return               the Tags activated as a result of excitation
     */
    private Set<Tag> excite(KnowledgeNode knowledgeNode) {
        Set<Tag> pendingActiveTags = new HashSet<>();
        knowledgeNode.activation++;
        if (knowledgeNode.activation * knowledgeNode.strength >= knowledgeNode.threshold) {
            pendingActiveTags = fire(knowledgeNode);
        }
        return pendingActiveTags;
    }

    /**
     * Fires a Knowledge Node.
     *
     * @param knowledgeNode  the Knowledge Node to fire
     * @return               the Tags activated as a result of firing
     */
    private Set<Tag> fire(KnowledgeNode knowledgeNode) {
        Set<Tag> pendingActiveTags = new HashSet<>();
        for (Tag outputTag : knowledgeNode.outputTags) {
            if (!activeTags.contains(outputTag)) {
                pendingActiveTags.add(outputTag);
            }
        }
        return pendingActiveTags;
    }
}
