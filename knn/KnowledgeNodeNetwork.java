package knn;

import tags.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Knowledge Node Network
 */
public class KnowledgeNodeNetwork {
    // Combine firedKNs and firedTags (redundant): just need fired Strings. Yes
    // Perhaps have a Tag class with Recommendation, Rule, Fact subclasses (good OOP design), or simply a flag specifying the type (in the database itself, we can still store as strings). Yes (Json possibility)
    // Once again changed data structures to sets (Is order important?) Good.
    // Spec data structures
    private HashMap<Tag, KnowledgeNode> mapKN; // Key here is the "inputTag" field of KnowledgeNode
    private Set<Tag> firedTags; // "Facts", activated tags stored here Is this the right way to store firedTags? Yes
    private Set<TupleNN> activeTuplesNN; // How do these TupleNN tuples translate to the String KnowledgeNode tags? What do we do with this? (Will need some interface to translate int + string to string)
    private Set<Tag> activeTagsMETA; // Commands coming from meta

    // TODO: Add Sigmoid cache + sigmoid function (perhaps in outside class)

    // Added structures
    int numberOfCycles; // Number of times the network should update its state and propogate. For now, just go to quiescence.

    public KnowledgeNodeNetwork(String dbFilename) { // What kind of database are we using? Probably CSV for now
        mapKN = new HashMap<>();
        activeTuplesNN = new HashSet<>();
        activeTagsMETA = new HashSet<>();
        firedTags = new HashSet<>();
    }

    public void reset(String dbFilename) {

    }

    public void resetEmpty() {
        clearKN();
        clearMETA();
        clearNN();
    }

    public void saveKNN(String dbFilename) {

    }

    public void addTupleNN(TupleNN nn) {
        activeTuplesNN.add(nn);
    }

    public void addMETA(Tag t) {
        activeTagsMETA.add(t);
    }


    public void clearNN() {
        activeTuplesNN.clear();
    }

    public void clearMETA() {
        activeTagsMETA.clear();
    }

    public void clearKN() {
        mapKN.clear();
        firedTags.clear();
    }

    public void addKN(KnowledgeNode kn) {
        mapKN.put(kn.inputTag, kn);
    }

    public void delKN(String hashTag) {
        mapKN.remove(hashTag);
    }

    public void addFiredTag(Tag tag) {
        firedTags.add(tag);
    }

    public Set<Tag> getFiredTags() {
        return firedTags;
    }

    /**
     * Chooses one of the 3 think methods. Assumes active ArrayLists are populated.
     *
     * @return
     */
    // How does this method choose the proper think method? For now, it will decide based on command from META. (activeTagsMETA)
    // What does think() return? The activated KNs? Yes. Passed on to ExpertSystem
    public Set<Tag> think() {
        return thinkForwards(); // What we want in the future: If no RECOMMENDATIONS fired = thinkForwards failed, resort to either thinkBackwards or thinkLambda (Not theoretically well understood)
        // People generally thinkBackwards all the time in the background. In the future, could have a background thread that thinks backwards...
    }

    public void think(int numberOfCycles) {
        thinkForwards(numberOfCycles);
    }

    /**
     * Given output tags, the system attempts to find the associated input tags with some degree of confidence
     */
    private void thinkBackwards() {
        Set<Tag> pendingFacts = new HashSet<>();
        do {
            pendingFacts.clear();
            for (KnowledgeNode kn : mapKN.values()) {
                boolean inputActivated = true;
                for (Tag t : kn.outputTags) {
                    if (!firedTags.contains(t)) { // Currently only activates input if ALL output tags are true
                        inputActivated = false;
                        break;
                    }
                }
                if (inputActivated)
                    pendingFacts.add(kn.inputTag);
            }
            for (Tag t : pendingFacts) {
                firedTags.add(t);
            }
        } while (!pendingFacts.isEmpty());
    }

    /**
     * Combination of thinkBackwards and thinkForwards. First the networks works backwards, then moves forward to determine the correct memory.
     */
    private void thinkLambda() {
        thinkBackwards();
        thinkForwards();
    }

    /**
     * Simple forward activation of knowledge nodes. If input tag activated and activation > threshold: output tags are activated, and this is cascaded through the network.
     */
    private Set<Tag> thinkForwards() { // How is a cycle defined? Just activate current tags in Facts, no more
        Set<Tag> newlyFiredTags = new HashSet<>();
        Set<Tag> pendingTags;
        do {
            pendingTags = forwardThinkCycle();
            firedTags.addAll(pendingTags);
            newlyFiredTags.addAll(pendingTags);
        } while(!pendingTags.isEmpty());
        return newlyFiredTags;
    }

    private Set<Tag> thinkForwards(int numberOfCycles) {
        Set<Tag> newlyFiredTags = new HashSet<>();
        for (int i = 0; i < numberOfCycles; i++) {
            Set<Tag> pendingTags = forwardThinkCycle();
            if (pendingTags.isEmpty()) {
                break;
            }
            firedTags.addAll(pendingTags);
            newlyFiredTags.addAll(pendingTags);
        }
        return newlyFiredTags;
    }

    private Set<Tag> forwardThinkCycle() { // returns true if tag fired
        Set<Tag> pendingFiredTags = new HashSet<>();
        for (Tag t : firedTags) {
            if (mapKN.containsKey(t)) { // If firedTags are updated after firing...
                Set<Tag> firedTags = excite(mapKN.get(t));
                if (firedTags != null && !firedTags.isEmpty()) {
                    pendingFiredTags.addAll(firedTags);
                }
            }
        }
        firedTags.addAll(pendingFiredTags);
        return pendingFiredTags;
    }

    private Set<Tag> excite(KnowledgeNode knowledgeNode) { // Returns true if firing occurs AND firedTags updated after firing
        knowledgeNode.activation++;
        if (knowledgeNode.activation >= knowledgeNode.threshold) {
            return fire(knowledgeNode);
        }
        return null;
    }

    private Set<Tag> fire(KnowledgeNode knowledgeNode) { // Returns Set of fired firedTags
        Set<Tag> pendingFacts = new HashSet<>();
        for (Tag outputTag : knowledgeNode.outputTags) {
            if (!firedTags.contains(outputTag)) {
                pendingFacts.add(outputTag);
            }
        }
        return pendingFacts.isEmpty() ? null : pendingFacts;

    }
}
