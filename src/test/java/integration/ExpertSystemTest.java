package integration;

import com.google.inject.Guice;
import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class ExpertSystemTest {
    private ExpertSystem es;

    @BeforeMethod
    public void setUp() throws Exception {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        es = prometheus.getExpertSystem();
    }

    @Test
    public void testES() {
        System.out.println();
        System.out.println("testES with Confidence Values");
        Fact[] testFacts = {
                new Fact("Aardvark(brown,strange,speed=slow", 0.9),
                new Fact("Bat(black,speed=10)", 0.6)
        };
        Recommendation recX = new Recommendation("Run(north,quickly,speed!=slow)", 0.2);
        Recommendation recY = new Recommendation("Hide(safelocation,manner=stealth)", 0.8);
        Recommendation recZ = new Recommendation("Fight(aggressive)", 0.8);
        Recommendation[] testRecommendations = {recX, recY};
        Fact[] outputPredicates1 = {new Fact("Dog(friendly,breed=pug,age<2)", 0.3)};
        Fact[] outputPredicates2 = {new Fact("Elephant(&x,size=big,intelligent)", 0.1)};
        Fact[] outputPredicates3 = {new Fact("Frog(colour=green,slimy,sound=ribbit)", 0.8)};
        Fact[] outputPredicates4 = {new Fact("Hog(&x,size=huge,&y,big)", 0.5)};
        Rule unactivatedRule = new Rule(new Fact[]{new Fact("Goose(loud,nationality=canadian,wingspan=4)", 0.5), new Fact("Aardvark(brown,?,speed=slow)", 0.8)}, testFacts);
        Rule rule1 = new Rule(new Fact[]{new Fact("Aardvark(brown,strange,?)", 0.2), new Fact("Bat(black,speed>9,*)", 0.7)}, outputPredicates1);
        Rule rule2 = new Rule(new Fact[]{new Fact("Dog(&x,breed=pug,age=1)", 0.7), new Fact("Bat(*)", 1.0)}, outputPredicates2);
        Rule rule3 = new Rule(new Fact[]{new Fact("Dog(friendly,breed=pug,age=1)", 0.9), new Fact("Elephant(friendly,size=big,intelligent)", 0.8)}, outputPredicates3);
        Rule rule4 = new Rule(new Fact[]{new Fact("Frog(&x,slimy,&y)", 0.6), new Fact("Elephant(*)", 0.7)}, outputPredicates4);
        Rule rule5 = new Rule(new Fact[]{new Fact("Hog(*)", 0.2), new Fact("Frog(?,slimy,sound=ribbit)", 0.9)}, new Predicate[]{recZ});
        Rule[] testRules = {
                rule1,
                rule2,
                rule3,
                rule5,
                unactivatedRule,
                rule4
        };

        for (Fact fact : testFacts) {
            es.addFact(fact);
        }
        for (Rule rule : testRules) {
            es.addReadyRule(rule);
        }
        for (Recommendation recommendation : testRecommendations) {
            es.addRecommendation(recommendation);
        }

        Set<Fact> initialFacts = es.getFacts();
        Set<Fact> expectedInitialFacts = new HashSet<>();
        expectedInitialFacts.addAll(Arrays.asList(testFacts));
        assertEquals(initialFacts, expectedInitialFacts);
        System.out.println("[ES] Initial facts: " + initialFacts);

        Set<Recommendation> initialRecommendations = es.getRecommendations();
        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
        expectedInitialRecommendations.addAll(Arrays.asList(testRecommendations));
        assertEquals(initialRecommendations, expectedInitialRecommendations);
        System.out.println("[ES] Initial recommendations: " + initialRecommendations);

        Set<Rule> initialRules = es.getReadyRules();
        Set<Rule> expectedInitialRules = new HashSet<>();
        expectedInitialRules.addAll(Arrays.asList(testRules));
        assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules: " + initialRules);

        Set<Recommendation> activatedRecommendations = es.think();
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        expectedActivatedRecommendations.add(recZ);
        assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(Arrays.asList(testFacts));
        expectedFacts.addAll(Arrays.asList(outputPredicates1));
        expectedFacts.addAll(Arrays.asList(outputPredicates2));
        expectedFacts.addAll(Arrays.asList(outputPredicates3));
        expectedFacts.addAll(Arrays.asList(outputPredicates4));
        assertEquals(facts, expectedFacts);
        System.out.println("[ES] Final facts: " + facts);

        Set<Recommendation> recommendations = es.getRecommendations();
        Set<Recommendation> expectedRecommendations = new HashSet<>();
        expectedRecommendations.add(recX);
        expectedRecommendations.add(recY);
        expectedRecommendations.add(recZ);
        assertEquals(recommendations, expectedRecommendations);
        System.out.println("[ES] Final recommendations: " + recommendations);

        Set<Rule> readyRules = es.getReadyRules();
        Set<Rule> expectedReadyRules = new HashSet<>();
        expectedReadyRules.add(unactivatedRule);
        assertEquals(expectedReadyRules, readyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);

        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        expectedActiveRules.add(rule5);
        assertEquals(expectedActiveRules, activeRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }

    @Test
    public void testESCycles() {
        System.out.println();
        System.out.println("testESCycles");
        Fact[] testFacts = {
                new Fact("Aardvark(brown,strange,speed=slow"),
                new Fact("Bat(black,speed=10)")
        };
        Recommendation recX = new Recommendation("Run(north,quickly,speed!=slow)");
        Recommendation recY = new Recommendation("Hide(safelocation,manner=stealth)");
        Recommendation recZ = new Recommendation("Fight(aggressive)");
        Recommendation[] testRecommendations = {recX, recY};
        Fact[] outputPredicates1 = {new Fact("Dog(friendly,breed=pug,age<2)")};
        Fact[] outputPredicates2 = {new Fact("Elephant(&x,size=big,intelligent)")};
        Fact[] outputPredicates3 = {new Fact("Frog(colour=green,slimy,sound=ribbit)")};
        Fact[] outputPredicates4 = {new Fact("Hog(&x,size=huge,&y,big)")};
        Fact outputFact = new Fact("Iguana(!big)");
        Rule unactivatedRule = new Rule(new Fact[]{new Fact("Goose(loud,nationality=canadian,wingspan=4)"), new Fact("Aardvark(brown,?,speed=slow)")}, outputPredicates4);
        Rule rule1 = new Rule(new Fact[]{new Fact("Aardvark(brown,strange,?)"), new Fact("Bat(black,speed>9)")}, outputPredicates1);
        Rule rule2 = new Rule(new Fact[]{new Fact("Dog(&x,breed=pug,age=1)"), new Fact("Bat(*)")}, outputPredicates2);
        Rule rule3 = new Rule(new Fact[]{new Fact("Dog(friendly,breed=pug,age=1)"), new Fact("Elephant(friendly,size=big,intelligent)")}, outputPredicates3);
        Rule rule4 = new Rule(new Fact[]{new Fact("Frog(&x,slimy,&y)"), new Fact("Elephant(*)")}, outputPredicates4);
        Rule rule5 = new Rule(new Fact[]{new Fact("Hog(*)"), new Fact("Frog(?,slimy,sound=ribbit)")}, new Predicate[]{recZ, outputFact});
        Rule rule6 = new Rule(new Fact[]{new Fact("Hog(*)"), new Fact("Iguana(small)")}, new Predicate[]{recY});
        Rule[] testRules = {
                rule1,
                rule2,
                rule3,
                rule5,
                unactivatedRule,
                rule4,
                rule6
        };

        for (Fact fact : testFacts) {
            es.addFact(fact);
        }
        for (Rule rule : testRules) {
            es.addReadyRule(rule);
        }
        for (Recommendation recommendation : testRecommendations) {
            es.addRecommendation(recommendation);
        }

        Set<Fact> initialFacts = es.getFacts();
        Set<Fact> expectedInitialFacts = new HashSet<>();
        expectedInitialFacts.addAll(Arrays.asList(testFacts));
        assertEquals(initialFacts, expectedInitialFacts);
        System.out.println("[ES] Initial facts: " + initialFacts);

        Set<Recommendation> initialRecommendations = es.getRecommendations();
        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
        expectedInitialRecommendations.addAll(Arrays.asList(testRecommendations));
        assertEquals(initialRecommendations, expectedInitialRecommendations);
        System.out.println("[ES] Initial recommendations: " + initialRecommendations);

        Set<Rule> initialRules = es.getReadyRules();
        Set<Rule> expectedInitialRules = new HashSet<>();
        expectedInitialRules.addAll(Arrays.asList(testRules));
        assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules: " + initialRules);

        Set<Recommendation> activatedRecommendations = es.think(false, 5);
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        expectedActivatedRecommendations.add(recZ);
        assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(Arrays.asList(testFacts));
        expectedFacts.addAll(Arrays.asList(outputPredicates1));
        expectedFacts.addAll(Arrays.asList(outputPredicates2));
        expectedFacts.addAll(Arrays.asList(outputPredicates3));
        expectedFacts.addAll(Arrays.asList(outputPredicates4));
        expectedFacts.add(outputFact);
        assertEquals(facts, expectedFacts);
        System.out.println("[ES] Final facts: " + facts);

        Set<Recommendation> recommendations = es.getRecommendations();
        Set<Recommendation> expectedRecommendations = new HashSet<>();
        expectedRecommendations.add(recX);
        expectedRecommendations.add(recY);
        expectedRecommendations.add(recZ);
        assertEquals(recommendations, expectedRecommendations);
        System.out.println("[ES] Final recommendations: " + recommendations);


        Set<Rule> readyRules = es.getReadyRules();

        Set<Rule> expectedReadyRules = new HashSet<>();
        expectedReadyRules.add(unactivatedRule);
        expectedReadyRules.add(rule6);
        assertEquals(expectedReadyRules, readyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);


        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        expectedActiveRules.add(rule5);
        assertEquals(expectedActiveRules, activeRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }

    @Test
    public void testESandLearn() {
        System.out.println();
        System.out.println("testES&Learn");
        System.out.println("***Think Cycle***");
        Fact[] testFacts = {
                new Fact("Aardvark(brown,strange,speed=slow"),
                new Fact("Bat(black,speed=10)")
        };
        Recommendation recX = new Recommendation("Run(north,quickly,speed!=slow)");
        Recommendation recY = new Recommendation("Hide(safelocation,manner=stealth)");
        Recommendation recZ = new Recommendation("Fight(aggressive)");
        Recommendation[] testRecommendations = {recX, recY};
        Fact[] outputPredicates1 = {new Fact("Dog(friendly,breed=pug,age<2)")};
        Fact[] outputPredicates2 = {new Fact("Elephant(&x,size=big,intelligent)")};
        Fact[] outputPredicates3 = {new Fact("Frog(colour=green,slimy,sound=ribbit)")};
        Fact[] outputPredicates4 = {new Fact("Hog(&x,size=huge,&y,big)")};
        Rule unactivatedRule = new Rule(new Fact[]{new Fact("Goose(loud,nationality=canadian,wingspan=4)"), new Fact("Aardvark(brown,?,speed=slow)")}, outputPredicates4);
        Rule provenRule = new Rule(
                new Fact[]{
                        new Fact("Aardvark(brown,strange,speed=slow"),
                        new Fact("Bat(black,speed=10)")},
                new Predicate[]{
                        new Fact("Elephant(friendly,size=big,intelligent)"),
                        new Fact("Dog(friendly,breed=pug,age<2)"),
                        new Fact("Hog(colour=green,size=huge,sound=ribbit,big)"),
                        new Recommendation("Fight(aggressive)"),
                        new Fact("Frog(colour=green,slimy,sound=ribbit)")});
        Rule rule1 = new Rule(new Fact[]{new Fact("Aardvark(brown,strange,?)"), new Fact("Bat(black,speed>9)")}, outputPredicates1);
        Rule rule2 = new Rule(new Fact[]{new Fact("Dog(&x,breed=pug,age=1)"), new Fact("Bat(*)")}, outputPredicates2);
        Rule rule3 = new Rule(new Fact[]{new Fact("Dog(friendly,breed=pug,age=1)"), new Fact("Elephant(friendly,size=big,intelligent)")}, outputPredicates3);
        Rule rule4 = new Rule(new Fact[]{new Fact("Frog(&x,slimy,&y)"), new Fact("Elephant(*)")}, outputPredicates4);
        Rule rule5 = new Rule(new Fact[]{new Fact("Hog(*)"), new Fact("Frog(?,slimy,sound=ribbit)")}, new Predicate[]{recZ});
        Rule[] testRules = {
                rule1,
                rule2,
                rule3,
                rule5,
                unactivatedRule,
                rule4
        };

        for (Fact fact : testFacts) {
            es.addFact(fact);
        }
        for (Rule rule : testRules) {
            es.addReadyRule(rule);
        }
        for (Recommendation recommendation : testRecommendations) {
            es.addRecommendation(recommendation);
        }

        Set<Fact> initialFacts = es.getFacts();
        Set<Fact> expectedInitialFacts = new HashSet<>();
        expectedInitialFacts.addAll(Arrays.asList(testFacts));
        assertEquals(initialFacts, expectedInitialFacts);
        System.out.println("[ES] Initial facts: " + initialFacts);

        Set<Recommendation> initialRecommendations = es.getRecommendations();
        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
        expectedInitialRecommendations.addAll(Arrays.asList(testRecommendations));
        assertEquals(initialRecommendations, expectedInitialRecommendations);
        System.out.println("[ES] Initial recommendations: " + initialRecommendations);

        Set<Rule> initialRules = es.getReadyRules();
        Set<Rule> expectedInitialRules = new HashSet<>();
        expectedInitialRules.addAll(Arrays.asList(testRules));
        assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules: " + initialRules);

        Set<Recommendation> activatedRecommendations = es.think(true);
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        expectedActivatedRecommendations.add(recZ);
        assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(Arrays.asList(testFacts));
        expectedFacts.addAll(Arrays.asList(outputPredicates1));
        expectedFacts.addAll(Arrays.asList(outputPredicates2));
        expectedFacts.addAll(Arrays.asList(outputPredicates3));
        expectedFacts.addAll(Arrays.asList(outputPredicates4));
        assertEquals(facts, expectedFacts);
        System.out.println("[ES] Final facts: " + facts);

        Set<Recommendation> recommendations = es.getRecommendations();
        Set<Recommendation> expectedRecommendations = new HashSet<>();
        expectedRecommendations.add(recX);
        expectedRecommendations.add(recY);
        expectedRecommendations.add(recZ);
        assertEquals(recommendations, expectedRecommendations);
        System.out.println("[ES] Final recommendations: " + recommendations);


        Set<Rule> readyRules = es.getReadyRules();

        Set<Rule> expectedReadyRules = new HashSet<>();
        expectedReadyRules.add(unactivatedRule);
        expectedReadyRules.add(provenRule);
        assertEquals(expectedReadyRules, readyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);

        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        expectedActiveRules.add(rule5);
        assertEquals(expectedActiveRules, activeRules);
        System.out.println("[ES] Final active rules: " + activeRules);

        System.out.println();
        System.out.println("***Rest Cycle***");

        es.rest(1);
        for (Rule rule : new HashSet<>(readyRules)) {
            es.removeReadyRule(rule);
        }
        Set<Rule> expectedRestRules = new HashSet<>();
        expectedRestRules.add(new Rule(
                new Fact[]{
                        new Fact("Goose(loud,nationality=canadian,wingspan=4)"),
                        new Fact("Aardvark(brown,?,speed=slow)")},
                new Predicate[]{
                        new Fact("Hog(colour=green,size=huge,sound=ribbit,big)")})
        );

        Rule expected = expectedRestRules.iterator().next();
        System.out.println("Expected rest rules: " + expectedReadyRules);
        Rule actual = readyRules.iterator().next();
        System.out.println("Ready rules: " + readyRules);

        System.out.println(expectedRestRules.equals(readyRules));
//        System.out.println(readyRules.equals(expectedRestRules));
//
//        System.out.println(actual.getInputFacts().equals(expected.getInputFacts()));
//        System.out.println(expected.getInputFacts().equals(actual.getInputFacts()));
//
//        System.out.println(actual.getOutputPredicates().equals(expected.getOutputPredicates()));
//        System.out.println(expected.getOutputPredicates().equals(actual.getOutputPredicates()));
//
//        System.out.println(actual.getOutputPredicates().iterator().next().equals(expected.getOutputPredicates().iterator().next()));
//        System.out.println(expected.getOutputPredicates().iterator().next().equals(actual.getOutputPredicates().iterator().next()));
//
//        System.out.println(actual.getOutputPredicates());
//        System.out.println(expected.getOutputPredicates());
//
//        System.out.println(actual.getOutputPredicates().hashCode());
//        System.out.println(expected.getOutputPredicates().hashCode());
//        System.out.println(expected.getOutputPredicates().iterator().next().hashCode());
//        System.out.println(actual.getOutputPredicates().iterator().next().hashCode());
//        System.out.println(expected.getOutputPredicates().iterator().next().hashCode());
//        System.out.println(actual.getOutputPredicates().iterator().next().hashCode());

        assertEquals(expected.getOutputPredicates(), actual.getOutputPredicates());
        assertEquals(expected.getOutputPredicates().iterator().next(), actual.getOutputPredicates().iterator().next());
        assertEquals(actual.getOutputPredicates().iterator().next(), expected.getOutputPredicates().iterator().next());
        assertEquals(actual.getOutputPredicates().iterator().next().getArguments(), expected.getOutputPredicates().iterator().next().getArguments());
        assertEquals(expected.getOutputPredicates().iterator().next().getArguments(), actual.getOutputPredicates().iterator().next().getArguments());
//        assertEquals(actual.getOutputPredicates(), expected.getOutputPredicates());

//        assertEquals(expected, actual); // TODO: Why does a.equals(b) but not b.equals(a) ?
//        assertEquals(actual, expected);
        System.out.println("[ES] Final rest rules: " + readyRules);
    }

    /**
     * Tests the teach functionality (basic) and Rule generation with OR of the ES.
     */

    @Test
    public void testESTeach() {
        System.out.println();
        System.out.println("testESTeach");

        String[] sampleSentences = {
                "If human(near) then @move(10)",
                "Do @retreat(quickly,carefully) when attacked()",
                "When battery(low) distance(!close) do @lowpowermode() battery(conservation)"
        };

        for (String sentence : sampleSentences) {
            es.teach(sentence);
        }

        List<Rule> multipleRules =
                Rule.makeRules("eyes(multiple) wings(2) OR legs(8) diet(carnivorous) -> insect() @investigate()");
        for (Rule rule : multipleRules) {
            es.addReadyRule(rule);
        }
        Set<Rule> taughtSentences = es.getReadyRules();
        Set<Rule> expectedTaughtSentences = new HashSet<>();

        Rule rule1 = new Rule(
                new Fact[]{new Fact("human(near")},
                new Predicate[]{new Recommendation("@move(10)")});
        Rule rule2 = new Rule(
                new Fact[]{new Fact("attacked()")},
                new Predicate[]{new Recommendation("@retreat(quickly,carefully)")});
        Rule rule3 = new Rule(
                new Fact[]{new Fact("eyes(multiple)"), new Fact("wings(2)")},
                new Predicate[]{new Fact("insect()"), new Recommendation("@investigate()")});
        Rule rule4 = new Rule(
                new Fact[]{new Fact("legs(8)"), new Fact("diet(carnivorous)")},
                new Predicate[]{new Fact("insect()"), new Recommendation("@investigate()")});

        Rule rule5 = new Rule("battery(low) distance(!close) -> @lowpowermode() battery(conservation)");

        expectedTaughtSentences.add(rule1);
        expectedTaughtSentences.add(rule2);
        expectedTaughtSentences.add(rule3);
        expectedTaughtSentences.add(rule4);
        expectedTaughtSentences.add(rule5);

        assertEquals(taughtSentences, expectedTaughtSentences);

        System.out.println("[ES] Final taught rules" + taughtSentences);
    }

}