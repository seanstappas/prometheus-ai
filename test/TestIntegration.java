package test;

import es.ExpertSystem;
import knn.KnowledgeNode;
import knn.KnowledgeNodeNetwork;
import knn.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tags.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Running es.ExpertSystem and knn.KnowledgeNodeNetwork
 */
public class TestIntegration { // TODO: test with Google's GSON libary
    KnowledgeNodeNetwork knn;
    ExpertSystem es;

    @BeforeTest
    public void setup() {
        knn = new KnowledgeNodeNetwork();
        es = new ExpertSystem();
    }

    /**
     * Tests the Knowledge Node Network's high-level functionality.
     */
    @Test
    public void testKNN() {
        System.out.println();
        System.out.println("***testKNN***");
        forwardSearchTest();
        forwardSearchWithPlyTest();
        backwardSearchTest();
        backwardSearchWithPlyTest();
        lambdaSearchTest();
    }

    /**
     * Test the forward Search of the Knowledge Node network
     *
     */
    public HashMap<Tag, Double> forwardSearchTest() {
        System.out.println("***Forward Search Test***");
        setupKNN();
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("dog", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("cat", 10);
        inputs.add(data2);

        knn.forwardSearch(inputs);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("calm(safe>5)"), 55.0);
        expectedActiveTags.put(new Fact("dog(wolflike,length>50,weight>20)"), 100.0);
        expectedActiveTags.put(new Fact("zoo(easy,attractive)"), 45.0);
        expectedActiveTags.put(new Fact("bark(sound,loud)"), 100.0);
        expectedActiveTags.put(new Fact("straightforward(smart,precise)"), 50.0);
        expectedActiveTags.put(new Fact("enemy(scary,dangerous)"), 40.5);
        expectedActiveTags.put(new Fact("loyal(easy,calm)"), 24.2);
        expectedActiveTags.put(new Fact("teeth(grind,food)"), 100.0);
        expectedActiveTags.put(new Rule("bark(sound,loud) pet(scary,attractive) -> @isPet(easy,calm,bark)"), 72.0);
        expectedActiveTags.put(new Fact("fast(speed,dynamic)"), 50.0);
        expectedActiveTags.put(new Rule("cat(feline,length>50,weight>20) dog(wolflike,length>50,weight>20) -> @fight(dangerous)"), 100.0);
        expectedActiveTags.put(new Fact("transport(easy,speed>10)"), 22.0);
        expectedActiveTags.put(new Fact("fur(strands,insulator)"), 100.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate,land)"), 68.75);
        expectedActiveTags.put(new Fact("food(easy,nutritious)"), 22.0);
        expectedActiveTags.put(new Fact("cat(feline,length>50,weight>20)"), 100.0);
        expectedActiveTags.put(new Fact("fish(vertebrate,water)"), 50.0);
        expectedActiveTags.put(new Fact("pet(scary,attractive)"), 44.0);
        expectedActiveTags.put(new Fact("confused(blur,prey)"), 55.0);
        expectedActiveTags.put(new Rule("massive(big,heavy) teeth(grind,food) -> @scary(scary,dangerous)"), 100.0);
        expectedActiveTags.put(new Rule("enemy(scary,dangerous) -> @avoid(scary,dangerous)"), 40.5);
        expectedActiveTags.put(new Rule("coward(scared,safe) loyal(easy,calm) -> @isSafe(easy,calm)"), 24.2);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after foward searching: " + activeTags);

        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }

    public HashMap<Tag, Double> forwardSearchWithPlyTest() {
        System.out.println("***Forward Search with Ply Test***");
        setupKNN();
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("dog", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("cat", 10);
        inputs.add(data2);

        knn.forwardSearch(inputs, 1);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("fast(speed,dynamic)"), 50.0);
        expectedActiveTags.put(new Rule("cat(feline,length>50,weight>20) dog(wolflike,length>50,weight>20) -> @fight(dangerous)"), 100.0);
        expectedActiveTags.put(new Fact("fur(strands,insulator)"), 100.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate,land)"), 68.75);
        expectedActiveTags.put(new Fact("dog(wolflike,length>50,weight>20)"), 100.0);
        expectedActiveTags.put(new Fact("cat(feline,length>50,weight>20)"), 100.0);
        expectedActiveTags.put(new Fact("bark(sound,loud)"), 100.0);
        expectedActiveTags.put(new Fact("fish(vertebrate,water)"), 50.0);
        expectedActiveTags.put(new Fact("teeth(grind,food)"), 100.0);
        expectedActiveTags.put(new Rule("massive(big,heavy) teeth(grind,food) -> @scary(scary,dangerous)"), 100.0);
        expectedActiveTags.put(new Rule("bark(sound,loud) pet(scary,attractive) -> @isPet(easy,calm,bark)"), 100.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after 0 ply forward searching: " + activeTags);

        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }

    public HashMap<Tag, Double> backwardSearchTest() {
        System.out.println("***Backward Search Test***");
        setupKNN();
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("calm", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("coward", 10);
        inputs.add(data2);

        knn.backwardSearch(inputs, 0.5);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("calm(safe>5)"), 100.0);
        expectedActiveTags.put(new Fact("fast(speed,dynamic)"), 40.0);
        expectedActiveTags.put(new Fact("fur(strands,insulator)"), 80.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate,land)"), 80.0);
        expectedActiveTags.put(new Fact("coward(scared,safe)"), 100.0);
        expectedActiveTags.put(new Fact("bird(vertebrate,air)"), 30.0);
        expectedActiveTags.put(new Fact("feathers(floating,insulator,flight)"), 30.0);
        expectedActiveTags.put(new Fact("dog(wolflike,length>50,weight>20)"), 60.0);
        expectedActiveTags.put(new Fact("bark(sound,loud)"), 80.0);
        expectedActiveTags.put(new Fact("chicken(eggs,length<50,weight<10)"), 30.0);
        expectedActiveTags.put(new Fact("sheep(wool,length>100,height>100,weight>50)"), 80.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after backward searching: " + activeTags);

        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }

    public HashMap<Tag, Double> backwardSearchWithPlyTest() {
        System.out.println("***Backward Search with Ply Test***");
        setupKNN();
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("calm", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("coward", 10);
        inputs.add(data2);

        knn.backwardSearch(inputs, 0.5, 2);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("calm(safe>5)"), 100.0);
        expectedActiveTags.put(new Fact("fast(speed,dynamic)"), 40.0);
        expectedActiveTags.put(new Fact("fur(strands,insulator)"), 80.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate,land)"), 80.0);
        expectedActiveTags.put(new Fact("coward(scared,safe)"), 100.0);
        expectedActiveTags.put(new Fact("bird(vertebrate,air)"), 30.0);
        expectedActiveTags.put(new Fact("feathers(floating,insulator,flight)"), 30.0);
        expectedActiveTags.put(new Fact("bark(sound,loud)"), 80.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after 2 ply backward searching: " + activeTags);

        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }

    public HashMap<Tag, Double> lambdaSearchTest() {
        System.out.println("***Lambda Search Test***");

        setupKNN();
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("mammal", 10);
        inputs.add(data1);
        String item = "fish(vertebrate,water)";

        Fact factToSearch = new Fact(item);

        knn.lambdaSearch(inputs, factToSearch);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("fur(strands,insulator)"), 100.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate,land)"), 100.0);
        expectedActiveTags.put(new Fact("cat(feline,length>50,weight>20)"), 100.0);
        expectedActiveTags.put(new Fact("fish(vertebrate,water)"), 70.0);
        expectedActiveTags.put(new Fact("teeth(grind,food)"), 100.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] String trying to find out a link with: " + item);
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after lambda searching: " + activeTags);

        Assert.assertTrue(activeTags.containsKey(factToSearch));
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }

    /**
     * Sets up a KNN by reading a input data file (in this case called petData.txt)
     */
    public void setupKNN() {
        knn.resetEmpty();
        ArrayList<KnowledgeNode> animal = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("./animalData")); //change the local directory for the test file to run
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split(";\\s+");
                KnowledgeNode kn = new KnowledgeNode(info);
                animal.add(kn);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("[KNN] Knowledge nodes in the KNN: ");
        for (KnowledgeNode anAnimal : animal) {
            knn.addKN(anAnimal);
            //System.out.println(animal.get(i).toString());
        }
        //System.out.println("");
    }

    @Test
    public void testES() {
        System.out.println();
        System.out.println("testES with Confidence Values");
        es.reset();
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
        Rule rule5 = new Rule(new Fact[]{new Fact("Hog(*)", 0.2), new Fact("Frog(?,slimy,sound=ribbit)", 0.9)}, new IPredicate[]{recZ});
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
        expectedFacts.addAll(Arrays.asList(outputPredicates1));
        expectedFacts.addAll(Arrays.asList(outputPredicates2));
        expectedFacts.addAll(Arrays.asList(outputPredicates3));
        expectedFacts.addAll(Arrays.asList(outputPredicates4));
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
        Assert.assertEquals(expectedReadyRules, readyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);

        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        expectedActiveRules.add(rule5);
        Assert.assertEquals(expectedActiveRules, activeRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }

    @Test
    public void testESCycles() {
        System.out.println();
        System.out.println("testESCycles");
        es.reset();
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
        Rule rule5 = new Rule(new Fact[]{new Fact("Hog(*)"), new Fact("Frog(?,slimy,sound=ribbit)")}, new IPredicate[]{recZ, outputFact});
        Rule rule6 = new Rule(new Fact[]{new Fact("Hog(*)"), new Fact("Iguana(small)")}, new IPredicate[]{recY});
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

        Set<Tag> activatedRecommendations = es.think(5, false);
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        expectedActivatedRecommendations.add(recZ);
        Assert.assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(Arrays.asList(testFacts));
        expectedFacts.addAll(Arrays.asList(outputPredicates1));
        expectedFacts.addAll(Arrays.asList(outputPredicates2));
        expectedFacts.addAll(Arrays.asList(outputPredicates3));
        expectedFacts.addAll(Arrays.asList(outputPredicates4));
        expectedFacts.add(outputFact);
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
        expectedReadyRules.add(rule6);
        Assert.assertEquals(expectedReadyRules, readyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);


        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        expectedActiveRules.add(rule5);
        Assert.assertEquals(expectedActiveRules, activeRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }

    @Test
    public void testESandLearn() {
        System.out.println();
        System.out.println("testES&Learn");
        System.out.println("***Think Cycle***");
        es.reset();
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
                new IPredicate[]{
                        new Fact("Elephant(friendly,size=big,intelligent)"),
                        new Fact("Dog(friendly,breed=pug,age<2)"),
                        new Fact("Hog(colour=green,size=huge,sound=ribbit,big)"),
                        new Recommendation("Fight(aggressive)"),
                        new Fact("Frog(colour=green,slimy,sound=ribbit)")});
        Rule rule1 = new Rule(new Fact[]{new Fact("Aardvark(brown,strange,?)"), new Fact("Bat(black,speed>9)")}, outputPredicates1);
        Rule rule2 = new Rule(new Fact[]{new Fact("Dog(&x,breed=pug,age=1)"), new Fact("Bat(*)")}, outputPredicates2);
        Rule rule3 = new Rule(new Fact[]{new Fact("Dog(friendly,breed=pug,age=1)"), new Fact("Elephant(friendly,size=big,intelligent)")}, outputPredicates3);
        Rule rule4 = new Rule(new Fact[]{new Fact("Frog(&x,slimy,&y)"), new Fact("Elephant(*)")}, outputPredicates4);
        Rule rule5 = new Rule(new Fact[]{new Fact("Hog(*)"), new Fact("Frog(?,slimy,sound=ribbit)")}, new IPredicate[]{recZ});
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

        Set<Tag> activatedRecommendations = es.think(true);
        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
        expectedActivatedRecommendations.add(recZ);
        Assert.assertEquals(activatedRecommendations, expectedActivatedRecommendations);
        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);

        Set<Fact> facts = es.getFacts();
        Set<Fact> expectedFacts = new HashSet<>();
        expectedFacts.addAll(Arrays.asList(testFacts));
        expectedFacts.addAll(Arrays.asList(outputPredicates1));
        expectedFacts.addAll(Arrays.asList(outputPredicates2));
        expectedFacts.addAll(Arrays.asList(outputPredicates3));
        expectedFacts.addAll(Arrays.asList(outputPredicates4));
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
        expectedReadyRules.add(provenRule);
        Assert.assertEquals(expectedReadyRules, readyRules);
        System.out.println("[ES] Final ready rules: " + readyRules);

        Set<Rule> activeRules = es.getActiveRules();
        Set<Rule> expectedActiveRules = new HashSet<>();
        expectedActiveRules.add(rule1);
        expectedActiveRules.add(rule2);
        expectedActiveRules.add(rule3);
        expectedActiveRules.add(rule4);
        expectedActiveRules.add(rule5);
        Assert.assertEquals(expectedActiveRules, activeRules);
        System.out.println("[ES] Final active rules: " + activeRules);

        System.out.println();
        System.out.println("***Rest Cycle***");

        es.rest(1);
        es.getReadyRules().removeAll(readyRules);
        Set<Rule> restRules = es.getReadyRules();
        Set<Rule> expectedRestRules = new HashSet<>();
        expectedRestRules.add(new Rule(
                new Fact[]{
                        new Fact("Goose(loud,nationality=canadian,wingspan=4)"),
                        new Fact("Aardvark(brown,?,speed=slow)")},
                new IPredicate[]{
                        new Fact("Hog(colour=green,size=huge,sound=ribbit,big)")})
        );

        Assert.assertEquals(restRules.iterator().next(), expectedRestRules.iterator().next()); // TODO: Why does a.equals(b) but not b.equals(a) ?
        System.out.println("[ES] Final rest rules: " + restRules);
    }

    /**
     * Tests the teach functionality (basic) & Rule generation with OR of the ES.
     */

    @Test
    public void testESTeach() {
        System.out.println();
        System.out.println("testESTeach");
        es.reset();

        String[] sampleSentences = {
                "If human(near) then @move(10)",
                "Do @retreat(quickly,carefully) when attacked()",
                "When battery(low) distance(!close) do @lowpowermode() battery(conservation)"
        };

        for (String sentences : sampleSentences) {
            es.teach(sentences);
        }

        List<Rule> multipleRules =
                Rule.makeRules("eyes(multiple) wings(2) OR legs(8) diet(carnivorous) -> insect() @investigate()");
        for (Rule rule : multipleRules) {
            es.addRule(rule);
        }
        Set<Rule> taughtSentences = es.getReadyRules();
        Set<Rule> expectedTaughtSentences = new HashSet<>();

        Rule rule1 = new Rule(
                new Fact[]{new Fact("human(near")},
                new IPredicate[]{new Recommendation("@move(10)")});
        Rule rule2 = new Rule(
                new Fact[]{new Fact("attacked()")},
                new IPredicate[]{new Recommendation("@retreat(quickly,carefully)")});
        Rule rule3 = new Rule(
                new Fact[]{new Fact("eyes(multiple)"), new Fact("wings(2)")},
                new IPredicate[]{new Fact("insect()"), new Recommendation("@investigate()")});
        Rule rule4 = new Rule(
                new Fact[]{new Fact("legs(8)"), new Fact("diet(carnivorous)")},
                new IPredicate[]{new Fact("insect()"), new Recommendation("@investigate()")});

        Rule rule5 = new Rule("battery(low) distance(!close) -> @lowpowermode() battery(conservation)");

        expectedTaughtSentences.add(rule1);
        expectedTaughtSentences.add(rule2);
        expectedTaughtSentences.add(rule3);
        expectedTaughtSentences.add(rule4);
        expectedTaughtSentences.add(rule5);

        Assert.assertEquals(taughtSentences, expectedTaughtSentences);

        System.out.println("[ES] Final taught rules" + taughtSentences);
    }

    /**
     * Tests the ES and KNN together.
     */
    @Test
    public void testKNNandES() {
        System.out.println();
        System.out.println("testKNNandES");
        setupKNN();
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("dog", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("cat", 10);
        inputs.add(data2);
        knn.forwardSearch(inputs);
        HashMap<Tag, Double> activatedTags = knn.getActiveTags();
        es.reset();
        es.addTags(activatedTags.keySet());

        Set<Fact> expectedInitialFacts = new HashSet<>();
        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
        Set<Rule> expectedInitialRules = new HashSet<>();

        for (Tag t : activatedTags.keySet()) {
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
        System.out.println("[ES] Initial facts (for ES): " + initialFacts);

        Set<Rule> initialRules = es.getReadyRules();
        Assert.assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules (for ES): " + initialRules);

        Set<Tag> activatedRecommendations = es.think();
        System.out.println("[ES] Active recommendation (for Meta): " + activatedRecommendations);

        Set<Tag> expectedActivatedRecommendation = new HashSet<>();
        expectedActivatedRecommendation.add(new Recommendation("@avoid(scary,dangerous)"));
        expectedActivatedRecommendation.add(new Recommendation("@isPet(easy,calm,bark)"));
        Assert.assertEquals(activatedRecommendations, expectedActivatedRecommendation);
    }
}
