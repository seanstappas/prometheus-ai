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

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
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
        //lambdaSearchTest();
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
    public Set<Tag> forwardSearchTest(){
    	System.out.println("***Forward Search Test***");
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("dog", 10); inputs.add(data1);
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());

        Set<Tag> activatedTags = knn.getActiveTags();
        System.out.println("[KNN] Activated tags: " + activatedTags);
        
        knn.forwardSearch(inputs);
        Set<Tag> activeTags = knn.getActiveTags();
        Set<Tag> expectedActiveTags = new HashSet<>();
        expectedActiveTags.add(new Fact("dog(wolflike, length>50, weight>20)"));
        expectedActiveTags.add(new Fact("bark(sound, loud)"));
        expectedActiveTags.add(new Fact("teeth(grind, food)"));
        expectedActiveTags.add(new Fact("fur(strands, insulator)"));
        expectedActiveTags.add(new Fact("fast(speed, dynamic)"));
        expectedActiveTags.add(new Fact("mammal(vertebrate, land)"));
        expectedActiveTags.add(new Fact("fish(vertebrate, water)"));
        expectedActiveTags.add(new Fact("calm(safe>5)"));
        expectedActiveTags.add(new Fact("confused(blur, prey)"));
        expectedActiveTags.add(new Fact("straightforward(smart, precise)"));
        expectedActiveTags.add(new Fact("food(easy, nutritious)"));
        expectedActiveTags.add(new Fact("pet(scary, attractive)"));
        expectedActiveTags.add(new Fact("transport(easy, speed>10)"));
        expectedActiveTags.add(new Fact("zoo(easy, attractive)"));
        expectedActiveTags.add(new Fact("loyal(easy, calm)"));
        expectedActiveTags.add(new Fact("enemy(scary, dangerous)"));
        expectedActiveTags.add(new Recommendation("@isSafe(easy, calm)"));
        expectedActiveTags.add(new Recommendation("@isPet(easy, calm, bark)"));
        expectedActiveTags.add(new Recommendation("@avoid(scary, dangerous)"));
        
        System.out.println("[KNN] Active tags after searching: " + activeTags);
        
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("");

        return activeTags;
    }
    
    public Set<Tag> forwardSearchWithPlyTest(){
    	System.out.println("***Forward Search with Ply Test***");
    	setupKNN();
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("horse", 10); inputs.add(data1);
		Tuple data2 = new Tuple("chicken", 10); inputs.add(data2);
        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        
        Set<Tag> activatedTags = knn.getActiveTags();
        System.out.println("[KNN] Activated tags: " + activatedTags);
        
        knn.forwardSearch(inputs, 0);
        Set<Tag> activeTags = knn.getActiveTags();
        Set<Tag> expectedActiveTags = new HashSet<>();
        expectedActiveTags.add(new Fact("horse(fast, length>100, height>100, weight>50, speed=40)"));
        expectedActiveTags.add(new Fact("chicken(eggs, length<50, weight<10)"));
        expectedActiveTags.add(new Fact("feathers(floating, insulator, flight)"));
        expectedActiveTags.add(new Fact("teeth(grind, food)"));
        
        Assert.assertEquals(activeTags, expectedActiveTags);
        System.out.println("[KNN] Active tags after searching: " + activeTags);
        System.out.println("");

        return activeTags;
    }
    
    public Set<Tag> backwardSearchTest(){
    	System.out.println("***Backward Search Test***");
    	setupKNN();
    	knn.addFiredTag(new Fact("mammal(vertebrate, land)"));
		knn.addFiredTag(new Fact("teeth(grind, food)"));
		
		Set<Tag> activatedTags = knn.getActiveTags();
		System.out.println("[KNN] Activated tags: " + activatedTags);
		
		knn.backwardSearch(0.75, 50);
		Set<Tag> activeTags = knn.getActiveTags();
		Set<Tag> expectedActiveTags = new HashSet<>();
		expectedActiveTags.add(new Fact("mammal(vertebrate, land)"));
		expectedActiveTags.add(new Fact("teeth(grind, food)"));
		expectedActiveTags.add(new Fact("fur(strands, insulator)"));
		expectedActiveTags.add(new Fact("sheep(wool, length>100, height>100, weight>50)"));
		expectedActiveTags.add(new Fact("cat(feline, length>50, weight>20)"));
		expectedActiveTags.add(new Fact("horse(fast, length>100, height>100, weight>50, speed=40)"));
		System.out.println("[KNN] Active tags after searching: " + activeTags);
		
		Assert.assertEquals(activeTags, expectedActiveTags);				
		System.out.println("");
		
		return activeTags;
    }
    
    public Set<Tag> backwardSearchWithPlyTest(){
    	System.out.println("***Backward Search with Ply Test***");
    	setupKNN();
    	knn.addFiredTag(new Fact("pet(scary, attractive)"));
		knn.addFiredTag(new Fact("transport(easy, speed>10)"));
		
		Set<Tag> activatedTags = knn.getActiveTags();
		System.out.println("[KNN] Activated tags: " + activatedTags);
		
		knn.backwardSearch(0.5, 40, 2);
		Set<Tag> activeTags = knn.getActiveTags();
		//expectedActiveTags.add(new Fact("rogdoll(Katty,female,length<55,weight>24)"));
		Assert.assertTrue(activeTags.size()>=3);
		
		System.out.println("[KNN] Active tags after searching: " + activeTags);
		System.out.println("");
		
		return activeTags;
    }
//    
//    public Set<Tag> lambdaSearchTest(){
//    	System.out.println("***Lambda Search Test***");
//    	setupKNN();
//    	
//    	knn.addFiredTag(new Fact("animal(multicellular,vertebrate,invertebrate)"));
//    	Set<Tag> activatedTags = knn.getActiveTags();
//		System.out.println("[KNN] Activated tags: " + activatedTags);
//		System.out.println("[KNN] Lambda search item is pet(dog>100,cat>80)");
//		
//		knn.lambdaSearch("pet(dog>100,cat>80)");
//		Set<Tag> activeTags = knn.getActiveTags();
//		
//		Set<Tag> expectedActiveTags = new HashSet<>();
//		expectedActiveTags.add(new Fact("husky(Ranger,male,length>58,weight=26)"));
//		expectedActiveTags.add(new Fact("dog(wow, carnivore)"));
//		expectedActiveTags.add(new Fact("animal(multicellular,vertebrate,invertebrate)"));
//		expectedActiveTags.add(new Fact("pet(dog>100,cat>80)"));
//		Assert.assertEquals(activeTags, expectedActiveTags);
//		
//		System.out.println("[KNN] Active tags after searching: " + activeTags);
//		System.out.println("");
//		
//		return activeTags;
//    }
    
    /**
     * Tests the Expert System's matches method.
     */
    public void testMatches() {
        System.out.println();
        System.out.println("testMatches");
        es.reset();


        Fact fact1 = new Fact("A()");
        Fact fact2 = new Fact("B()");
        Fact fact3 = new Fact("A(height=low)");
        Fact fact31 = new Fact("A(height!=tall)");

        Fact fact4 = new Fact("A(?)");

        Fact fact5 = new Fact("A(height=10,weight>10)");
        Fact fact6 = new Fact("A(height=10,weight=12)");
        Fact fact7 = new Fact("A(height=10,*)");
        Fact fact8 = new Fact("A(height=7,*)");
        Fact fact9 = new Fact("B(large,distance!=far,?,object=human)");
        Fact fact10 = new Fact("B(large,distance=near,hello,object=human)");
        Fact fact11 = new Fact("D(window,material=glass,thickness!>2)");
        Fact fact12 = new Fact("D(window,*)");
        Fact fact121 = new Fact("D(window,?)");
        Fact fact13 = new Fact("D(window,door)");
        Fact fact14 = new Fact("F(temperature=40,hot,humidity=high)");
        Fact fact15 = new Fact("F(*)");
        Fact fact16 = new Fact("A(10,12,14)");
        Fact fact17 = new Fact("A(&x,12,&y)");

        /*
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact3.matches(fact31)[0]);
        Assert.assertTrue(fact5.matches(fact6)[0]);
        Assert.assertTrue(fact9.matches(fact10)[0]);
        //Assert.assertTrue(fact13.matches(fact121)[0]);
        //Assert.assertTrue(fact14.matches(fact15)[0]);
 		*/
        Assert.assertTrue(fact16.matches(fact17).doesMatch);
    }


    public void testESRest() {
        System.out.println();
        System.out.println("testESRest");
        es.reset();
        Rule[] testRules = {
                new Rule("A() -> B()"),
                new Rule("C() -> D()"),
                new Rule("B() -> C()"),
                new Rule("E() -> F()"),
                new Rule("D() -> E()"),
                new Rule("Z() -> G()")
        };
        Rule[] expectedNewRules = {
                new Rule("C() -> E()"),
                new Rule("A() -> C()"),
                new Rule("D() -> F()"),
                new Rule("B() -> D()")
        };

        for (Rule rule : testRules) {
            es.addRule(rule);
        }

        Set<Rule> initialRules = es.getReadyRules();
        Set<Rule> expectedInitialRules = new HashSet<>();
        expectedInitialRules.addAll(Arrays.asList(testRules));
        Assert.assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules: " + initialRules);

        HashSet<Rule> newRuleSet = es.ruleMerger(1);
        HashSet<Rule> expectedNewRuleList = new HashSet<>();
        expectedNewRuleList.addAll(Arrays.asList(expectedNewRules));


        Assert.assertEquals(newRuleSet, expectedNewRuleList);
        System.out.println("[ES] New rules: " + newRuleSet);


    }


    @Test
    public void testESThink() {
        System.out.println();
        System.out.println("testESThink");
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
//
//    /**
//     * Tests the ES and KNN together.
//     */
//    @Test
//    public void testKNNandES() {
//        System.out.println();
//        System.out.println("testKNNandES");
//        Set<Tag> activatedTags = setupKNNandThink();
//        es.reset();
//        es.addTags(activatedTags);
//
//        Set<Fact> expectedInitialFacts = new HashSet<>();
//        Set<Recommendation> expectedInitialRecommendations = new HashSet<>();
//        Set<Rule> expectedInitialRules = new HashSet<>();
//
//        for (Tag t : activatedTags) {
//            switch (t.type) {
//                case FACT:
//                    expectedInitialFacts.add((Fact) t);
//                    break;
//                case RULE:
//                    expectedInitialRules.add((Rule) t);
//                    break;
//                case RECOMMENDATION:
//                    expectedInitialRecommendations.add((Recommendation) t);
//                    break;
//            }
//        }
//        System.out.println("[ES] Initial activated tags (from KNN): " + activatedTags);
//
//        Set<Fact> initialFacts = es.getFacts();
//        Assert.assertEquals(initialFacts, expectedInitialFacts);
//        System.out.println("[ES] Initial facts: " + initialFacts);
//
//        Set<Recommendation> initialRecommendations = es.getRecommendations();
//        Assert.assertEquals(initialRecommendations, expectedInitialRecommendations);
//        System.out.println("[ES] Initial recommendations: " + initialRecommendations);
//
//        Set<Rule> initialRules = es.getReadyRules();
//        Assert.assertEquals(initialRules, expectedInitialRules);
//        System.out.println("[ES] Initial rules: " + initialRules);
//
//        Set<Tag> activatedRecommendations = es.think();
//        Set<Tag> expectedActivatedRecommendations = new HashSet<>();
//        Recommendation recZ = new Recommendation("Z()");
//        expectedActivatedRecommendations.add(recZ);
//        Assert.assertEquals(activatedRecommendations, expectedActivatedRecommendations);
//        System.out.println("[ES] Activated recommendations: " + activatedRecommendations);
//
//        Set<Fact> facts = es.getFacts();
//        Set<Fact> expectedFacts = new HashSet<>();
//        expectedFacts.addAll(expectedInitialFacts);
//        Assert.assertEquals(facts, expectedFacts);
//        System.out.println("[ES] Final facts: " + facts);
//
//        Set<Recommendation> recommendations = es.getRecommendations();
//        Set<Recommendation> expectedRecommendations = new HashSet<>();
//        expectedRecommendations.add(recZ);
//        Assert.assertEquals(recommendations, expectedRecommendations);
//        System.out.println("[ES] Final recommendations: " + recommendations);
//
//        Fact[] outputTagsE = {new Fact("H()"), new Fact("I()"), new Fact("J()")};
//        Tag outputTag4 = new Rule(
//                outputTagsE,
//                new Fact[]{new Recommendation("Z()")}
//        );
//
//        Set<Rule> readyRules = es.getReadyRules();
//        Set<Rule> expectedReadyRules = new HashSet<>();
//        Assert.assertEquals(readyRules, expectedReadyRules);
//        System.out.println("[ES] Final ready rules: " + readyRules);
//
//        Set<Rule> activeRules = es.getActiveRules();
//        Set<Tag> expectedActiveRules = new HashSet<>();
//        expectedActiveRules.add(outputTag4);
//        Assert.assertEquals(activeRules, expectedActiveRules);
//        System.out.println("[ES] Final active rules: " + activeRules);
//    }

}
