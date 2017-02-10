import java.util.ArrayList;
import java.util.HashMap;

/**
 * Knowledge Node Network
 */
public class KNN {
    private HashMap<String, KN> mapKN;
    private ArrayList<KN> activeKN;
    private ArrayList<NN> activeNN;
    private ArrayList<String> activeMETA;

    public KNN(String dbFilename) {
        mapKN = new HashMap<>();
        activeKN = new ArrayList<>();
        activeNN = new ArrayList<>();
        activeMETA = new ArrayList<>();
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
        mapKN.put(kn.hashTag, kn); // TODO: Is hashTag the map key?
    }

    public void delKN(String hashTag) {
        mapKN.remove(hashTag);
    }

    /**
     * Assumes active ArrayLists are populated
     *
     * @return
     */
    public ArrayList think() {
        return null;
    }

    private void thinkBackwards() {

    }

    private void thinkLambda() {

    }

    private void thinkForwards() {

    }

    class KN { // TODO: Right types?
        String hashTag;
        boolean activation;
        int threshold;
        int age;
        String[] strings; // TODO: What is this for?

        public KN(String hashTag, boolean activation, int threshold, int age, String[] strings) {
            this.hashTag = hashTag;
            this.activation = activation;
            this.threshold = threshold;
            this.age = age;
            this.strings = strings;
        }
    }

    class NN {
        String label;
        int measurement;

    }
}
