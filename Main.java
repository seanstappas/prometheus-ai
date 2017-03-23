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
        testKNN();
        testES();
//        testKNNandES();
    }

    /**
     * Tests the Knowledge Node Network.
     * @return the activated Tags.
     */
    private static Set<Tag> testKNN() {
        KnowledgeNodeNetwork knowledge = new KnowledgeNodeNetwork("database");
        Tag[] initialFiredTags = new Tag[] {
                new Fact("A")
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
        System.out.println("[KNN] All fired tags: " + knowledge.getActiveTags());
        return firedTags;
    }

    /**
     * Tests the Expert System (ES).
     */
    private static void testES() {
        ExpertSystem expert = new ExpertSystem();
        Fact[] testFacts = {
                new Fact("A"),
                new Fact("B")
        };
        Recommendation recX =  new Recommendation("X");
        Recommendation recY =  new Recommendation("Y");
        Recommendation recZ =  new Recommendation("Z");
        Recommendation[] testRecommendations = {
                recX,
                recY
        };
        Rule[] testRules = {
                new Rule("A", "B", "D"),
                new Rule("D", "B", "E"),
                new Rule("D", "E", "F"),
                new Rule("G", "A", "H"),
                new Rule(new Recommendation[]{recX, recY}, recZ)
        };
        for (Fact fact : testFacts) {
            expert.addFact(fact);
        }
        for (Rule rule : testRules) {
            expert.addRule(rule);
        }
        for (Recommendation recommendation : testRecommendations) {
            expert.addRecommendation(recommendation);
        }
        Set<Tag> activatedTags = expert.think();

        System.out.println("[ES] Initial facts: " + Arrays.toString(testFacts));
        System.out.println("[ES] Initial recommendations: " + Arrays.toString(testRecommendations));
        System.out.println("[ES] Initial rules: " + Arrays.toString(testRules));

        System.out.println("[ES] Activated tags: " + activatedTags);

        System.out.println("[ES] Final facts: " + expert.getFacts());
        System.out.println("[ES] Final recommendations: " + expert.getRecommendations());

        System.out.println("[ES] Final ready rules: " + expert.getReadyRules());
        System.out.println("[ES] Final activated rules: " + expert.getActiveRules());
    }

    /**
     * Tests the ES and KNN together.
     */
    private static void testKNNandES() {
        Set<Tag> firedTags = testKNN();

        ExpertSystem es = new ExpertSystem();
        es.addTags(firedTags);
    }
}
