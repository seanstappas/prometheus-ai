package test;

import es.ExpertSystem;
import knn.KnowledgeNode;
import knn.KnowledgeNodeNetwork;
import knn.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

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
     * Sets up a KNN by reading a input data file (in this case called petData.txt)
     */
    public void setupKNN() {
        knn.resetEmpty();
        ArrayList<KnowledgeNode> animal = new ArrayList<>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader("./animalData")); //change the local directory for the test file to run
			String line;
			while( (line = br.readLine()) != null){
				String[] info = line.split(";\\s+");
				KnowledgeNode kn = new KnowledgeNode(info);
				animal.add(kn);
			}
			br.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		//System.out.println("[KNN] Knowledge nodes in the KNN: ");
		for(int i=0; i<animal.size(); i++){
			knn.addKN(animal.get(i));
			//System.out.println(animal.get(i).toString());
		}		
		//System.out.println("");
    }
    
    /**
     * Test the forward Search of the Knowledge Node network
     * 
     * @return 
     */
    public HashMap<Tag, Double> forwardSearchTest(){
    	System.out.println("***Forward Search Test***");
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("dog", 10); inputs.add(data1);
		Tuple data2 = new Tuple("cat", 10); inputs.add(data2);                
        
        knn.forwardSearch(inputs);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("calm(safe>5)"), 55.0);
        expectedActiveTags.put(new Fact("confused(blur, prey)"), 55.0);
        expectedActiveTags.put(new Fact("loyal(easy, calm)"), 24.2);
        expectedActiveTags.put(new Fact("straightforward(smart, precise)"), 50.0);
        expectedActiveTags.put(new Fact("fur(strands, insulator)"), 100.0);
        expectedActiveTags.put(new Fact("zoo(easy, attractive)"), 45.0);
        expectedActiveTags.put(new Fact("dog(wolflike, length>50, weight>20)"), 100.0);
        expectedActiveTags.put(new Fact("transport(easy, speed>10)"), 22.0);
        expectedActiveTags.put(new Fact("bark(sound, loud)"), 100.0);
        expectedActiveTags.put(new Fact("pet(scary, attractive)"), 44.0);
        expectedActiveTags.put(new Fact("enemy(scary, dangerous)"), 40.5);
        expectedActiveTags.put(new Fact("cat(feline, length>50, weight>20)"), 100.0);
        expectedActiveTags.put(new Recommendation("@isSafe(easy, calm)"), 24.2);
        expectedActiveTags.put(new Fact("teeth(grind, food)"), 100.0);
        expectedActiveTags.put(new Fact("fish(vertebrate, water)"), 50.0);
        expectedActiveTags.put(new Recommendation("@isPet(easy, calm, bark)"), 42.0);
        expectedActiveTags.put(new Fact("food(easy, nutritious)"), 22.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate, land)"), 68.75);
        expectedActiveTags.put(new Fact("fast(speed, dynamic)"), 50.0);
        expectedActiveTags.put(new Recommendation("@avoid(scary, dangerous)"), 40.5);
        
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after foward searching: " + activeTags);
        
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }
    
    public HashMap<Tag, Double> forwardSearchWithPlyTest(){
    	System.out.println("***Forward Search with Ply Test***");
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("tuna", 10); inputs.add(data1);
		Tuple data2 = new Tuple("shark", 10); inputs.add(data2);                
        
        knn.forwardSearch(inputs, 0);
        HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("teeth(grind, food)"), 100.0);
        expectedActiveTags.put(new Fact("tuna(flock, length<200)"), 100.0);
        expectedActiveTags.put(new Fact("swarm(together)"), 80.0);
        expectedActiveTags.put(new Fact("shark(predator, length>50, speed>10)"), 100.0);
        expectedActiveTags.put(new Fact("massive(big, heavy)"), 50.0);
        
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after 0 ply forward searching: " + activeTags);
        
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");
        
        return activeTags;
    }
    
    public HashMap<Tag, Double> backwardSearchTest(){
    	System.out.println("***Backward Search Test***");
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("calm", 10); inputs.add(data1);
		Tuple data2 = new Tuple("coward", 10); inputs.add(data2); 		
		
		knn.backwardSearch(inputs, 0.5);
		HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        expectedActiveTags.put(new Fact("calm(safe>5)"), 100.0);
        expectedActiveTags.put(new Fact("coward(scared, safe)"), 100.0);
        expectedActiveTags.put(new Fact("sheep(wool, length>100, height>100, weight>50)"), 80.0);
        expectedActiveTags.put(new Fact("fur(strands, insulator)"), 80.0);
        expectedActiveTags.put(new Fact("chicken(eggs, length<50, weight<10)"), 30.0);
        expectedActiveTags.put(new Fact("shark(predator, length>50, speed>10)"), 40.0);
        expectedActiveTags.put(new Fact("dog(wolflike, length>50, weight>20)"), 55.0);
        expectedActiveTags.put(new Fact("bark(sound, loud)"), 80.0);
        expectedActiveTags.put(new Fact("cat(feline, length>50, weight>20)"), 60.0);
        expectedActiveTags.put(new Fact("feathers(floating, insulator, flight)"), 30.0);
        expectedActiveTags.put(new Fact("teeth(grind, food)"), 40.0);
        expectedActiveTags.put(new Fact("horse(fast, length>100, height>100, weight>50, speed=40)"), 40.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate, land)"), 80.0);
        expectedActiveTags.put(new Fact("fast(speed, dynamic)"), 40.0);
        expectedActiveTags.put(new Fact("bird(vertebrate, air)"), 30.0);
		
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after backward searching: " + activeTags);
        
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");
        
		return activeTags;
    }
    
    public HashMap<Tag, Double> backwardSearchWithPlyTest(){
    	System.out.println("***Backward Search with Ply Test***");
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("calm", 10); inputs.add(data1);
		Tuple data2 = new Tuple("coward", 10); inputs.add(data2);
		
		knn.backwardSearch(inputs, 0.5, 2);
		HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();        
        expectedActiveTags.put(new Fact("calm(safe>5)"), 100.0);
        expectedActiveTags.put(new Fact("bark(sound, loud)"), 80.0);
        expectedActiveTags.put(new Fact("coward(scared, safe)"), 100.0);
        expectedActiveTags.put(new Fact("feathers(floating, insulator, flight)"), 30.0);
        expectedActiveTags.put(new Fact("teeth(grind, food)"), 40.0);
        expectedActiveTags.put(new Fact("mammal(vertebrate, land)"), 80.0);
        expectedActiveTags.put(new Fact("fur(strands, insulator)"), 80.0);
        expectedActiveTags.put(new Fact("fast(speed, dynamic)"), 40.0);
        expectedActiveTags.put(new Fact("bird(vertebrate, air)"), 30.0);
        
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after 2 ply backward searching: " + activeTags);
        
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");
		
		return activeTags;
    }
    
    public HashMap<Tag, Double> lambdaSearchTest(){
    	System.out.println("***Lambda Search Test***");
    	
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("mammal", 10); inputs.add(data1);
		String item = "@avoid(scary, dangerous)";
		
		knn.lambdaSearch(inputs, item);
		HashMap<Tag, Double> inputTags = knn.getInputTags();
        HashMap<Tag, Double> activeTags = knn.getActiveTags();
        HashMap<Tag, Double> expectedActiveTags = new HashMap<>();
        
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] String trying to find out a link with: " + item);
        System.out.println("[KNN] Input tags found in KNN: " + inputTags.toString());
        System.out.println("[KNN] Active tags after lambda searching: " + activeTags);
        
        Assert.assertTrue(activeTags.containsKey(new Recommendation("@avoid(scary, dangerous)")));
        Assert.assertTrue(activeTags.get(new Recommendation("@avoid(scary, dangerous)")) == 56.7);
        System.out.println("");
		
		return activeTags;
    }

    @Test
    public void testES() {
        System.out.println();
        System.out.println("testES");
        es.reset();
        Fact[] testFacts = {
                new Fact("A(height=10,length>12,!fast)"),
                new Fact("B(10)")
        };
        Recommendation recX = new Recommendation("X()");
        Recommendation recY = new Recommendation("Y()");
        Recommendation recZ = new Recommendation("Z()");
        Recommendation[] testRecommendations = {recX, recY};
        Fact[] outputTags1 = {new Fact("D(10)")};
        Fact[] outputTags2 = {new Fact("E(&x,height=5)")};
        Fact[] outputTags3 = {new Fact("F(temperature=40,hot,humidity!=medium)")};
        Fact[] outputTags4 = {new Fact("H(&x,hot,&y,big)")};
        ArrayList<Rule> ruleOr = Rule.makeRules("P1(ARG1,ARG2) P2(ARG3) OR P3(ARG4,ARG5) P4(ARG6,ARG7) OR P4(arg1,arg2,arg3) -> @P3(ARG4,ARG5,ARG6)");
        Rule ruleone = new Rule("P(A,B,C) P(D) -> P3(9,1,1)");
        Rule unactivatedRule = new Rule(new Fact[]{new Fact("G()"), new Fact("A(*)")}, outputTags4);
        Rule rule1 = new Rule(new Fact[]{new Fact("A(height=10,length=15,slow)"), new Fact("B(10)")}, outputTags1);
        Rule rule2 = new Rule(new Fact[]{new Fact("D(&x)"), new Fact("B(10)")}, outputTags2);
        Rule rule3 = new Rule(new Fact[]{new Fact("D(10)"), new Fact("E(10,?)")}, outputTags3);
        Rule rule4 = new Rule(new Fact[]{new Fact("F(&x,hot,&y)"), new Fact("E(10,?)")}, outputTags4);
        Rule rule5 = new Rule(new Fact[]{new Fact("H(*)"), new Fact("F(temperature=40,hot,humidity=large)")}, new Fact[]{recZ});
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

        Set<Tag> activatedRecommendations = es.think(false);
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
        expectedFacts.addAll(Arrays.asList(outputTags4));
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
        expectedActiveRules.add(rule5);
        Assert.assertEquals(activeRules, expectedActiveRules);
        System.out.println("[ES] Final active rules: " + activeRules);
    }

    /**
     * Tests the ES and KNN together.
     */
    public void testKNNandES() {
        System.out.println();
        System.out.println("testKNNandES");
        knn.resetEmpty();
        forwardSearchTest();

        Set<Tag> activatedTags = new HashSet<>();
        for (Map.Entry<Tag, Double> entry : knn.getActiveTags().entrySet()) {
            Tag tag = entry.getKey();
            double confidenceValue = entry.getValue() / 100;
            tag.setConfidenceValue(confidenceValue);
            activatedTags.add(tag);
        }

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
        Recommendation recZ = new Recommendation("Z()");
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

        Fact[] outputTagsE = {new Fact("H()"), new Fact("I()"), new Fact("J()")};
        Tag outputTag4 = new Rule(
                outputTagsE,
                new Fact[]{new Recommendation("Z()")}
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
