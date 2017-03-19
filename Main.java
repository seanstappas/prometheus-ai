import es.ES;
import knn.KNN;
import tags.Rule;

import java.util.Arrays;
import java.util.Set;

/**
 * Running es.ES and knn.KNN
 */
public class Main {
    // TODO: How will transfer happen between ES and KNN? Will there be an outer Main?
    public static void main(String[] args) { // TODO: Test ES and KNN together
        testKNN();
        testES();
        testKNNandES();
    }

    private static void testKNN() {
        KNN knowledge = new KNN("database");
        String[] initialFacts = new String[] {"A"};
        for (String fact : initialFacts) {
            knowledge.addFiredTag(fact);
        }
        knowledge.addKN("A", new String[]{"B", "C", "D"});
        knowledge.addKN("B", new String[]{"E", "F", "G"});
        knowledge.addKN("E", new String[]{"H", "I", "J"});
        Set<String> firedTags = knowledge.think();
        System.out.println("[KNN] Initial tags: " + Arrays.toString(initialFacts));
        System.out.println("[KNN] Newly fired tags: " + firedTags);
        System.out.println("[KNN] All fired tags: " + knowledge.getFiredTags());
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
        System.out.println("[ES] Final ready rules: " + expert.getReadyRules());
        System.out.println("[ES] Final activated rules: " + expert.getActivatedRules());
    }

    private static void testKNNandES() {

    }
}
