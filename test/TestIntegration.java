package test;

import es.ExpertSystem;
import knn.KnowledgeNode;
import knn.KnowledgeNodeNetwork;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Running es.ExpertSystem and knn.KnowledgeNodeNetwork
 */
public class TestIntegration { // TODO: test with Google's GSON libary
    KnowledgeNodeNetwork knn;
    ExpertSystem es;

    @BeforeTest
    public void setup() {
        knn = new KnowledgeNodeNetwork("database");
        es = new ExpertSystem();

    }

    /**
     * Tests the Knowledge Node Network's high-level functionality.
     */
    @Test
    public void testKNN() {
        System.out.println();
        System.out.println("testKNN");
        setupKNNandThink();
    }

    /**
     * Sets up a KNN, makes it think(), and returns the Tags activated as a result of thinking.
     *
     * @return the Tags activated as a result of thinking
     */
    public Set<Tag> setupKNNandThink() {
        knn.resetEmpty();
        Tag[] initialActiveTags = new Tag[]{
                new Fact("A")
        };
        for (Tag t : initialActiveTags) {
            knn.addFiredTag(t);
        }
        Tag inTag4 = new Fact("D");
        Tag[] outputTagsA = {new Fact("B"), new Fact("C"), new Fact("D")};
        Tag[] outputTagsB = {new Fact("E"), new Fact("F"), new Fact("G")};
        Fact[] outputTagsE = {new Fact("H"), new Fact("I"), new Fact("J")};
        Tag outputTag4 = new Rule(
                outputTagsE,
                new Tag[]{new Recommendation("Z")}
        );
        KnowledgeNode[] knowledgeNodes = new KnowledgeNode[]{
                new KnowledgeNode(new Fact("A"), outputTagsA),
                new KnowledgeNode(new Fact("B"), outputTagsB),
                new KnowledgeNode(new Fact("E"), outputTagsE),
                new KnowledgeNode(inTag4, new Tag[]{outputTag4})
        };
        for (KnowledgeNode node : knowledgeNodes) {
            knn.addKN(node);
        }

        System.out.println("[KNN] Initial knowledge nodes: " + Arrays.toString(knowledgeNodes));
        System.out.println("[KNN] Initial active tags: " + Arrays.toString(initialActiveTags));

        Set<Tag> activatedTags = knn.think();
        Set<Tag> expectedActivatedTags = new HashSet<>();
        expectedActivatedTags.addAll(Arrays.asList(outputTagsA));
        expectedActivatedTags.addAll(Arrays.asList(outputTagsB));
        expectedActivatedTags.addAll(Arrays.asList(outputTagsE));
        expectedActivatedTags.add(outputTag4);
        Assert.assertEquals(activatedTags, expectedActivatedTags);
        System.out.println("[KNN] Newly activated tags: " + activatedTags);

        Set<Tag> activeTags = knn.getActiveTags();
        Set<Tag> expectedActiveTags = new HashSet<>();
        expectedActiveTags.addAll(expectedActivatedTags);
        expectedActiveTags.addAll(Arrays.asList(initialActiveTags));
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("[KNN] All active tags: " + activeTags);

        return activatedTags;
    }

    /**
     * Tests the Expert System's high-level functionality.
     */
    @Test
    public void testES() {
        System.out.println();
        System.out.println("testES");
        es.reset();
        Fact[] testFacts = {
                new Fact("A"),
                new Fact("B")
        };
        Recommendation recX = new Recommendation("X");
        Recommendation recY = new Recommendation("Y");
        Recommendation recZ = new Recommendation("Z");
        Recommendation[] testRecommendations = {recX, recY};
        Fact[] outputTags1 = {new Fact("D")};
        Fact[] outputTags2 = {new Fact("E")};
        Fact[] outputTags3 = {new Fact("F")};
        Fact[] outputTags4 = {new Fact("H")};
        Rule unactivatedRule = new Rule(new Fact[]{new Fact("G"), new Fact("A")}, outputTags4);
        Rule rule1 = new Rule(new Fact[]{new Fact("A"), new Fact("B")}, outputTags1);
        Rule rule2 = new Rule(new Fact[]{new Fact("D"), new Fact("B")}, outputTags2);
        Rule rule3 = new Rule(new Fact[]{new Fact("D"), new Fact("E")}, outputTags3);
        Rule rule4 = new Rule(new Fact[]{new Fact("E"), new Fact("F")}, new Tag[]{recZ});
        Rule[] testRules = {
                rule1,
                rule2,
                rule3,
                unactivatedRule,
                rule4
        };

        for (Fact fact : testFacts) {
            es.addFact(fact);
        }
        for (Rule rule : testRules) {
            es.addRule(rule);
        }
        for (Recommendation recommendation : testRecommendations) {
            es.addRecommendation(recommendation);
        }

        Set<Fact> initialFacts = es.getFacts();
        Set<Fact> expectedInitialFacts = new HashSet<>();
        expectedInitialFacts.addAll(Arrays.asList(testFacts));
        Assert.assertEquals(initialFacts, expectedInitialFacts);
        System.out.println("[ES] Initial facts: " + initialFacts);

        Set<Recommendation> initialRecommendations = es.getRecommendations();
        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
        expectedInitialRecommendations.addAll(Arrays.asList(testRecommendations));
        Assert.assertEquals(initialRecommendations, expectedInitialRecommendations);
        System.out.println("[ES] Initial recommendations: " + initialRecommendations);

        Set<Rule> initialRules = es.getReadyRules();
        Set<Rule> expectedInitialRules = new HashSet<>();
        expectedInitialRules.addAll(Arrays.asList(testRules));
        Assert.assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules: " + initialRules);

        Set<Tag> activatedRecommendations = es.think();
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        expectedActivatedRecommendations.add(recZ);
        Assert.assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(Arrays.asList(testFacts));
        expectedFacts.addAll(Arrays.asList(outputTags1));
        expectedFacts.addAll(Arrays.asList(outputTags2));
        expectedFacts.addAll(Arrays.asList(outputTags3));
        Assert.assertEquals(facts, expectedFacts);
        System.out.println("[ES] Final facts: " + facts);

        Set<Recommendation> recommendations = es.getRecommendations();
        Set<Recommendation> expectedRecommendations = new HashSet<>();
        expectedRecommendations.add(recX);
        expectedRecommendations.add(recY);
        expectedRecommendations.add(recZ);
        Assert.assertEquals(recommendations, expectedRecommendations);
        System.out.println("[ES] Final recommendations: " + recommendations);

        Set<Rule> readyRules = es.getReadyRules();
        Set<Rule> expectedReadyRules = new HashSet<>();
        expectedReadyRules.add(unactivatedRule);
        Assert.assertEquals(readyRules, expectedReadyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);

        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        Assert.assertEquals(activeRules, expectedActiveRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }

    /**
     * Tests the ES and KNN together.
     */
    @Test
    public void testKNNandES() {
        System.out.println();
        System.out.println("testKNNandES");
        Set<Tag> activatedTags = setupKNNandThink();
        es.reset();
        es.addTags(activatedTags);

        Set<Fact> expectedInitialFacts = new HashSet<>();
        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
        Set<Rule> expectedInitialRules = new HashSet<>();

        for (Tag t : activatedTags) {
            switch (t.type) {
                case FACT:
                    expectedInitialFacts.add((Fact) t);
                    break;
                case RULE:
                    expectedInitialRules.add((Rule) t);
                    break;
                case RECOMMENDATION:
                    expectedInitialRecommendations.add((Recommendation) t);
                    break;
            }
        }
        System.out.println("[ES] Initial activated tags (from KNN): " + activatedTags);

        Set<Fact> initialFacts = es.getFacts();
        Assert.assertEquals(initialFacts, expectedInitialFacts);
        System.out.println("[ES] Initial facts: " + initialFacts);

        Set<Recommendation> initialRecommendations = es.getRecommendations();
        Assert.assertEquals(initialRecommendations, expectedInitialRecommendations);
        System.out.println("[ES] Initial recommendations: " + initialRecommendations);

        Set<Rule> initialRules = es.getReadyRules();
        Assert.assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules: " + initialRules);

        Set<Tag> activatedRecommendations = es.think();
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        Recommendation recZ = new Recommendation("Z");
        expectedActivatedRecommendations.add(recZ);
        Assert.assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(expectedInitialFacts);
        Assert.assertEquals(facts, expectedFacts);
        System.out.println("[ES] Final facts: " + facts);

        Set<Recommendation> recommendations = es.getRecommendations();
        Set<Recommendation> expectedRecommendations = new HashSet<>();
        expectedRecommendations.add(recZ);
        Assert.assertEquals(recommendations, expectedRecommendations);
        System.out.println("[ES] Final recommendations: " + recommendations);

        Fact[] outputTagsE = {new Fact("H"), new Fact("I"), new Fact("J")};
        Tag outputTag4 = new Rule(
                outputTagsE,
                new Tag[]{new Recommendation("Z")}
        );

        Set<Rule> readyRules = es.getReadyRules();
        Set<Rule> expectedReadyRules = new HashSet<>();
        Assert.assertEquals(readyRules, expectedReadyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);

        Set<Rule> activeRules = es.getActiveRules();
        Set<Tag> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(outputTag4);
        Assert.assertEquals(activeRules, expectedActiveRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }
}
