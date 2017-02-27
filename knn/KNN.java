package knn;

import java.util.*;

/**
 * Knowledge Node Network
 */
public class KNN {
    // TODO: Once again changed data structures to sets (Is order important?)
    private HashMap<String, KN> mapKN; // Key here is the "hashTag" field of KN
    private Set<KN> activeKN; // TODO: Are these the KNs whose activation > threshold?
    private Set<NN> activeNN; // TODO: How do these NN tuples translate to the String KN tags?
    private Set<String> activeMETA; // TODO: Are these the "true" (active) tags in the system? Or is this just a set of META layers? If so, how do we keep track of activated tags? Should there be a list of "facts"?
    int numberOfCycles; // Number of times the network should update its state and propogate. For now, just go to quiescence. TODO: Is this a field? How will this be set?

    // TODO: How to handle "facts"...
    private Set<String> facts;

    public KNN(String dbFilename) {
        mapKN = new HashMap<>();
        activeKN = new HashSet<>();
        activeNN = new HashSet<>();
        activeMETA = new HashSet<>();
        facts = new HashSet<>();
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

    public void addNN(NN nn) {
        activeNN.add(nn);
    }

    public void addMETA(String s) {
        activeMETA.add(s);
    }

    public void addKN(KN kn) {
        activeKN.add(kn);
    }

    public void clearNN() {
        activeNN.clear();
    }

    public void clearMETA() {
        activeMETA.clear();
    }

    public void clearKN() {
        activeKN.clear();
    }

    public void newKN(KN kn) {
        mapKN.put(kn.hashTag, kn);
    }

    public void delKN(String hashTag) {
        mapKN.remove(hashTag);
    }

    /**
     * Chooses one of the 3 think methods. Assumes active ArrayLists are populated.
     *
     * @return
     */
    public ArrayList think() {
        return null;
    } // TODO: How does this method choose the proper think method?

    /**
     * Given output tags, the system attempts to find the associated input tags with some degree of confidence
     */
    private void thinkBackwards() {

    }

    /**
     * Combination of thinkBackwards and thinkForwards. First the networks works backwards, then moves forward to determine the correct memory.
     */
    private void thinkLambda() {

    }

    /**
     * Simple forward activation of knowledge nodes. If input tag activated and activation > threshold: output tags are activated, and this is cascaded through the network.
     */
    private void thinkForwards() {
        Set<String> pendingFacts = new HashSet<>();
        do {
            pendingFacts.clear();
            for(String fact : facts) {
                KN kn = mapKN.get(fact);
                if (kn != null) {
                    Collections.addAll(pendingFacts, kn.strings);
                }
            }
            for(String s : pendingFacts) {
                facts.add(s);
            }
        } while (!pendingFacts.isEmpty());
    }

    class KN { // TODO: Right types?
        String hashTag;
        int activation; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
        int threshold; // limit: When activation > threshold : fires output tags (strings array). These tags can be lists of rules or facts.
        int age; // When a node is newly formed it has an age of zero.
                 // When the node’s age increases to a value greater than or equal to K the node is then deleted.
                 // The age parameter ages in a particular way.  It ages only if it is not used.  Every time a node is used the age is reset to zero.
                 // If the node is not used after an “tau” amount of time it will age.
                 // Ages linearly or using sigmoid.
        int strength;
        int confidence;
        String[] strings; // TODO: What is this for? These are the output tags, fired when activation > threshold.

        public KN(String hashTag, int activation, int threshold, int age, int strength, int confidence, String[] strings) {
            this.hashTag = hashTag;
            this.activation = activation;
            this.threshold = threshold;
            this.age = age;
            this.strength = strength;
            this.confidence = confidence;
            this.strings = strings;
        }
    }

    class NN {
        String label;
        int measurement;

    }
}
