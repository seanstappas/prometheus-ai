import es.ExpertSystem;
import knn.KnowledgeNode;
import knn.KnowledgeNodeNetwork;
import tags.Rule;
import tags.Tag;
import tags.TagType;

import java.util.Arrays;
import java.util.Set;

/**
 * Running es.ExpertSystem and knn.KnowledgeNodeNetwork
 */
public class Main { // TODO: Test with Google's GSON libary
    public static void main(String[] args) { // TODO: Test ExpertSystem and KnowledgeNodeNetwork together
        testKNN();
        testES();
        testKNNandES();
    }

    private static Set<Tag> testKNN() {
        KnowledgeNodeNetwork knowledge = new KnowledgeNodeNetwork("database");
        Tag[] initialFiredTags = new Tag[] {
                new Tag("A", TagType.FACT)
        };
        for (Tag t : initialFiredTags) {
            knowledge.addFiredTag(t);
        }
        KnowledgeNode[] knowledgeNodes = new KnowledgeNode[] {
                new KnowledgeNode("A", new String[]{"B", "C", "D"}),
                new KnowledgeNode("B", new String[]{"E", "F", "G"}),
                new KnowledgeNode("E", new String[]{"H", "I", "J"})
        };
        for (KnowledgeNode node : knowledgeNodes) {
            knowledge.addKN(node);
        }
        Set<Tag> firedTags = knowledge.think();
        System.out.println("[KnowledgeNodeNetwork] Initial fired tags: " + Arrays.toString(initialFiredTags));
        System.out.println("[KnowledgeNodeNetwork] Newly fired tags: " + firedTags);
        System.out.println("[KnowledgeNodeNetwork] All fired tags: " + knowledge.getFiredTags());
        return firedTags;
    }

    private static void testES() {
        ExpertSystem expert = new ExpertSystem();
        Tag[] testFacts = {
                new Tag("A", TagType.FACT),
                new Tag("B", TagType.FACT)
        };
        Rule[] testRules = {
                new Rule("A", "B", "D"),
                new Rule("D", "B", "E"),
                new Rule("D", "E", "F"),
                new Rule("G", "A", "H")
        };
        for (Tag fact : testFacts) {
            expert.addFact(fact);
        }
        for (Rule rule : testRules) {
            expert.addRule(rule);
        }
        expert.think();

        System.out.println("[ExpertSystem] Initial facts: " + Arrays.toString(testFacts));
        System.out.println("[ExpertSystem] Final facts: " + expert.getFacts());

        System.out.println("[ExpertSystem] Initial rules: " + Arrays.toString(testRules));
        System.out.println("[ExpertSystem] Final ready rules: " + expert.getReadyRules());
        System.out.println("[ExpertSystem] Final activated rules: " + expert.getActivatedRules());
    }

    private static void testKNNandES() {
        Set<Tag> firedTags = testKNN();

        ExpertSystem es = new ExpertSystem();
        es.addTags(firedTags);
    }
}
