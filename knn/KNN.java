package knn;

import java.util.*;

/**
 * Knowledge Node Network
 */
public class KNN {

    // Once again changed data structures to sets (Is order important?) Good.
    // Spec data structures
    private HashMap<String, KN> mapKN; // Key here is the "hashTag" field of KN
    private Set<KN> activeKN; // Are these the KNs whose activation > threshold? What do we do with this? Fired KNs (to check if need to fire others)
    private Set<NN> activeNN; // How do these NN tuples translate to the String KN tags? What do we do with this? (Will need some interface to translate int + string to string)
    private Set<String> activeMETA; // Commands coming from meta
    // TODO: Change activeKN from Set<KN> to Set<String> (since KN uniquely identifiable with tags, and we are already storing them in mapKN: unnecessary to store twice)

    // Added structures
    int numberOfCycles; // Number of times the network should update its state and propogate. For now, just go to quiescence.
    private Set<String> facts; // "Facts", activated tags stored here Is this the right way to store facts? Yes

    public KNN(String dbFilename) { // What kind of database are we using? Probably CSV for now
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
    // How does this method choose the proper think method? For now, it will decide based on command from META. (activeMETA)
    // TODO: What does think() return? ...
    public ArrayList think() {
        thinkForwards(); // What we want in the future: If no RECOMMENDATIONS fired = thinkForwards failed, resort to either thinkBackwards or thinkLambda (Not theoretically well understood)
        // People generally thinkBackwards all the time in the background. In the future, could have a background thread that thinks backwards...
        return null;
    }

    /**
     * Given output tags, the system attempts to find the associated input tags with some degree of confidence
     */
    private void thinkBackwards() {
        Set<String> pendingFacts = new HashSet<>();
        do {
            pendingFacts.clear();
            for (KN kn : mapKN.values()) {
                boolean inputActivated = true;
                for (String s : kn.strings) {
                    if (!facts.contains(s)) { // Currently only activates input if ALL output tags are true
                        inputActivated = false;
                        break;
                    }
                }
                if (inputActivated)
                    pendingFacts.add(kn.hashTag);
            }
            for (String s : pendingFacts) {
                facts.add(s);
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
    private void thinkForwards() { // How is a cycle defined? Just activate current tags in Facts, no more
        Set<String> pendingFacts = new HashSet<>();
        do {
            pendingFacts.clear();
            for (String fact : facts) {
                if (mapKN.containsKey(fact)) { // If facts are updated after firing...
                    Set<String> firedTags = mapKN.get(fact).excite();
                    if (firedTags != null && !firedTags.isEmpty()) {
                        pendingFacts.addAll(firedTags);
                    }
                }
            }
            for (String pendingFact : pendingFacts) {
                facts.add(pendingFact);
            }
        } while (!pendingFacts.isEmpty());
    }

    // Added methods

    public void addFact(String fact) {
        facts.add(fact);
    }

    public void newKN(String inputTag, String[] outputTags) {
        KN kn = new KN(inputTag, outputTags);
        mapKN.put(inputTag, kn);
    }

    public Set<String> getFacts() {
        return facts;
    }

    class KN {
        String hashTag;
        int activation = 0; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
        // Is threshold the same for all nodes? No
        int threshold = 1; // limit: When activation > threshold : fires output tags (strings array). These tags can be lists of rules or facts.
        int age = 0; // When a node is newly formed it has an age of zero.
        // When the node’s age increases to a value greater than or equal to K the node is then deleted.
        // The age parameter ages in a particular way.  It ages only if it is not used.  Every time a node is used the age is reset to zero.
        // If the node is not used after an “tau” amount of time it will age.
        // Ages linearly or using sigmoid.
        int strength; // How is strength used? Read doc...
        int confidence; // How is confidence used? Read doc...
        String[] strings; // What is this for? These are the output tags, fired when activation > threshold.

        public KN(String hashTag, String[] strings) {
            this.hashTag = hashTag;
            this.strings = strings;
        }

        // Added methods
        Set<String> excite() { // Returns true if firing occurs AND facts updated after firing
            activation++;
            if (activation >= threshold) {
                return fire();
            }
            return null;
        }

        private Set<String> fire() { // Returns true if facts updated after firing
            Set<String> pendingFacts = new HashSet<>();
            for (String outputTag : strings) {
                if (!facts.contains(outputTag)) {
                    pendingFacts.add(outputTag);
                }
            }
            return pendingFacts.isEmpty() ? null : pendingFacts;

        }

        // TODO: Should age increment at every think() cycle? Or independently, after some amount of time? System will have (daily) timestamp, nodes will have timestamp updated at every firing. Look at difference between the two before deciding to fire
        public void age() {
            age++;
        }
    }

    class NN {
        String label;
        int measurement;

    }
}
