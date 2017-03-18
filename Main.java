import es.ES;
import tags.Rule;
import knn.KNN;

import java.util.Arrays;

/**
 * Running es.ES and knn.KNN
 */
public class Main {
    public static void main(String[] args) { // TODO: Test ES and KNN together
        testES();
        testKNN();
    }

    private static void testKNN() {
        KNN knowledge = new KNN("database");
        String[] initialFacts = new String[] {"A"};
        for (String fact : initialFacts) {
            knowledge.addFact(fact);
        }
        knowledge.newKN("A", new String[]{"B", "C", "D"});
        knowledge.newKN("B", new String[]{"E", "F", "G"});
        knowledge.newKN("E", new String[]{"H", "I", "J"});
        knowledge.think();
        System.out.println("[KNN] Initial facts: " + Arrays.toString(initialFacts));
        System.out.println("[KNN] Final facts: " + knowledge.getFacts());
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

        System.out.println("[ES] Initial facts: " + Arrays.toString(testFacts));
        System.out.println("[ES] Final facts: " + expert.getFacts());

        System.out.println("[ES] Initial rules: " + Arrays.toString(testRules));
        System.out.println("[ES] Final rules: " + expert.getRules());
    }
}
