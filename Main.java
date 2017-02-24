import es.ES;
import es.Rule;

import java.util.Arrays;

/**
 * Running es.ES and knn.KNN
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
        String[] testFacts = {"A", "B"};
        Rule[] testRules = {
                new Rule("A", "B", "D"),
                new Rule("D", "B", "E"),
                new Rule("D", "E", "F"),
                new Rule("G", "A", "H")
        };
        for (String fact : testFacts) {
            expert.addFact(fact);
        }
        for (Rule rule : testRules) {
            expert.addRule(rule);
        }
        expert.think();

        System.out.println("Initial facts: " + Arrays.toString(testFacts));
        System.out.println("Final facts: " + expert.getFacts());

        System.out.println("Initial rules: " + Arrays.toString(testRules));
        System.out.println("Final rules: " + expert.getRules());
    }
}
