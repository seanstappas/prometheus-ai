import es.ExpertSystem;
import knn.KnowledgeNode;
import knn.KnowledgeNodeNetwork;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.Arrays;
import java.util.Set;

/**
 * Running es.ExpertSystem and knn.KnowledgeNodeNetwork
 */
public class Main { // TODO: Test with Google's GSON libary
    public static void main(String[] args) { // TODO: Test ExpertSystem and KnowledgeNodeNetwork together
//        testKNN();
        testES();
//        testKNNandES();
    }

    private static Set<Tag> testKNN() {
        KnowledgeNodeNetwork knowledge = new KnowledgeNodeNetwork("database");
        Tag[] initialFiredTags = new Tag[] {
                new Tag("A", Tag.Type.FACT)
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
        System.out.println("[KNN] Initial fired tags: " + Arrays.toString(initialFiredTags));
        System.out.println("[KNN] Newly fired tags: " + firedTags);
        System.out.println("[KNN] All fired tags: " + knowledge.getFiredTags());
        return firedTags;
    }

    private static void testES() {
        ExpertSystem expert = new ExpertSystem();
        Tag[] testFacts = {
                new Fact("A"),
                new Fact("B")
        };
        Tag recX =  new Recommendation("X");
        Tag recY =  new Recommendation("Y");
        Tag recZ =  new Recommendation("Z");
        Tag[] testRecommendations = {
                recX,
                recY
        };
        Rule[] testRules = {
                new Rule("A", "B", "D"),
                new Rule("D", "B", "E"),
                new Rule("D", "E", "F"),
                new Rule("G", "A", "H"),
                new Rule(new Tag[]{recX, recY}, recZ)
        };
        for (Tag fact : testFacts) {
            expert.addFact(fact);
        }
        for (Rule rule : testRules) {
            expert.addRule(rule);
        }
        for (Tag recommendation : testRecommendations) {
            expert.addRecommendation(recommendation);
        }
        expert.think();

        System.out.println("[ES] Initial facts: " + Arrays.toString(testFacts));
        System.out.println("[ES] Final facts: " + expert.getFacts());

        System.out.println("[ES] Initial recommendations: " + Arrays.toString(testRecommendations));
        System.out.println("[ES] Final recommendations: " + expert.getRecommendations());

        System.out.println("[ES] Initial rules: " + Arrays.toString(testRules));
        System.out.println("[ES] Final ready rules: " + expert.getReadyRules());
        System.out.println("[ES] Final activated rules: " + expert.getActivatedRules());
    }

    private static void testKNNandES() {
        Set<Tag> firedTags = testKNN();

        ExpertSystem es = new ExpertSystem();
        es.addTags(firedTags);
    }
}
