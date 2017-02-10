/**
 * Running ES and KNN
 */
public class Main {
    public static void main(String[] args) {
        testES();
        testKNN();
    }

    private static void testKNN() {

    }

    private static void testES() {
        ES expert = new ES();
        // Add facts and rules
        expert.think();
        // Send recommendations to META (or META pulls from ES)
    }
}
