import es.ES;
import es.Rule;
import knn.KNN;

import java.util.Arrays;

/**
 * Running es.ES and knn.KNN
 */
public class Main {
    public static void main(String[] args) {
//        testES();
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
        knowledge.newKN("C", new String[]{"H", "I", "J"});
        knowledge.think();
        System.out.println("Initial facts: " + Arrays.toString(initialFacts));
        System.out.println("Final facts: " + knowledge.getFacts());
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
