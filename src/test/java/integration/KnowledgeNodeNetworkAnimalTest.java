package integration;

import com.google.inject.Guice;
import knn.api.KnowledgeNodeNetwork;
import knn.api.Tuple;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Rule;
import tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class KnowledgeNodeNetworkAnimalTest {
    private static final String ANIMAL_DATA_PATH = "data/animalData.txt";
    private KnowledgeNodeNetwork knn;

    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
    }


    /**
     * Sets up a KNN by reading a input data file (in this case called petData.txt.txt)
     */
    @BeforeMethod
    public void setupKNN() {
        KnnDataLoader.loadData(knn, ANIMAL_DATA_PATH);
    }

    /**
     * Test the forward Search of the Knowledge Node network
     *
     */
    @Test
    public void forwardSearchTest() {
        System.out.println("***Forward Search Test***");
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("dog", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("cat", 10);
        inputs.add(data2);

        knn.forwardSearch(inputs);
        Set<Tag> activeTags = knn.getActiveTags();
        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();
        expectedActiveTagsAndBeliefs.put(new Fact("calm(safe>5)"), 55.0);
        expectedActiveTagsAndBeliefs.put(new Fact("dog(wolflike,length>50,weight>20)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("zoo(easy,attractive)"), 45.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bark(sound,loud)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("straightforward(smart,precise)"), 50.0);
        expectedActiveTagsAndBeliefs.put(new Fact("enemy(scary,dangerous)"), 40.5);
        expectedActiveTagsAndBeliefs.put(new Fact("loyal(easy,calm)"), 24.2);
        expectedActiveTagsAndBeliefs.put(new Fact("teeth(grind,food)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Rule("bark(sound,loud) pet(scary,attractive) -> @isPet(easy,calm,bark)"), 72.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fast(speed,dynamic)"), 50.0);
        expectedActiveTagsAndBeliefs.put(new Rule("cat(feline,length>50,weight>20) dog(wolflike,length>50,weight>20) -> @fight(dangerous)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("transport(easy,speed>10)"), 22.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fur(strands,insulator)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("mammal(vertebrate,land)"), 68.75);
        expectedActiveTagsAndBeliefs.put(new Fact("food(easy,nutritious)"), 22.0);
        expectedActiveTagsAndBeliefs.put(new Fact("cat(feline,length>50,weight>20)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fish(vertebrate,water)"), 50.0);
        expectedActiveTagsAndBeliefs.put(new Fact("pet(scary,attractive)"), 44.0);
        expectedActiveTagsAndBeliefs.put(new Fact("confused(blur,prey)"), 55.0);
        expectedActiveTagsAndBeliefs.put(new Rule("massive(big,heavy) teeth(grind,food) -> @scary(scary,dangerous)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Rule("enemy(scary,dangerous) -> @avoid(scary,dangerous)"), 40.5);
        expectedActiveTagsAndBeliefs.put(new Rule("coward(scared,safe) loyal(easy,calm) -> @isSafe(easy,calm)"), 24.2);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Active tags after foward searching: " + activeTags);

        assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        for (Tag t : activeTags) {
            assertEquals(knn.getKnowledgeNode(t).belief, 0d);
        }
        System.out.println("");

    }

    @Test
    public void forwardSearchWithPlyTest() {
        System.out.println("***Forward Search with Ply Test***");
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("dog", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("cat", 10);
        inputs.add(data2);

        knn.forwardSearch(inputs, 1);
        Set<Tag> activeTags = knn.getActiveTags();
        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();
        expectedActiveTagsAndBeliefs.put(new Fact("fast(speed,dynamic)"), 50.0);
        expectedActiveTagsAndBeliefs.put(new Rule("cat(feline,length>50,weight>20) dog(wolflike,length>50,weight>20) -> @fight(dangerous)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fur(strands,insulator)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("mammal(vertebrate,land)"), 68.75);
        expectedActiveTagsAndBeliefs.put(new Fact("dog(wolflike,length>50,weight>20)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("cat(feline,length>50,weight>20)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bark(sound,loud)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fish(vertebrate,water)"), 50.0);
        expectedActiveTagsAndBeliefs.put(new Fact("teeth(grind,food)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Rule("massive(big,heavy) teeth(grind,food) -> @scary(scary,dangerous)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Rule("bark(sound,loud) pet(scary,attractive) -> @isPet(easy,calm,bark)"), 100.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Active tags after 0 ply forward searching: " + activeTags);

        assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        for (Tag t : activeTags) {
            assertEquals(knn.getKnowledgeNode(t).belief, 0d);
        }
        System.out.println("");

    }

    @Test
    public void backwardSearchTest() {
        System.out.println("***Backward Search Test***");
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("calm", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("coward", 10);
        inputs.add(data2);

        knn.backwardSearch(inputs, 0.5);
        Set<Tag> activeTags = knn.getActiveTags();
        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();
        expectedActiveTagsAndBeliefs.put(new Fact("calm(safe>5)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fast(speed,dynamic)"), 40.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fur(strands,insulator)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("mammal(vertebrate,land)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("coward(scared,safe)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bird(vertebrate,air)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("feathers(floating,insulator,flight)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("dog(wolflike,length>50,weight>20)"), 60.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bark(sound,loud)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("chicken(eggs,length<50,weight<10)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("sheep(wool,length>100,height>100,weight>50)"), 80.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Active tags after backward searching: " + activeTags);

        assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        for (Tag t : activeTags) {
            assertEquals(knn.getKnowledgeNode(t).belief, 0d);
        }
        System.out.println("");
    }

    @Test
    public void backwardSearchWithPlyTest() {
        System.out.println("***Backward Search with Ply Test***");
        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("calm", 10);
        inputs.add(data1);
        Tuple data2 = new Tuple("coward", 10);
        inputs.add(data2);

        knn.backwardSearch(inputs, 0.5, 2);
        Set<Tag> activeTags = knn.getActiveTags();
        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();
        expectedActiveTagsAndBeliefs.put(new Fact("calm(safe>5)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fast(speed,dynamic)"), 40.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fur(strands,insulator)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("mammal(vertebrate,land)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("coward(scared,safe)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bird(vertebrate,air)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("feathers(floating,insulator,flight)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bark(sound,loud)"), 80.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] Active tags after 2 ply backward searching: " + activeTags);

        assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        for (Tag t : activeTags) {
            assertEquals(knn.getKnowledgeNode(t).belief, 0d);
        }
        System.out.println("");
    }

    @Test
    public void lambdaSearchTest() {
        System.out.println("***Lambda Search Test***");

        ArrayList<Tuple> inputs = new ArrayList<>();
        Tuple data1 = new Tuple("mammal", 10);
        inputs.add(data1);
        String item = "fish(vertebrate,water)";

        Fact factToSearch = new Fact(item);

        knn.lambdaSearch(inputs, factToSearch);
        Set<Tag> activeTags = knn.getActiveTags();
        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();
//        expectedActiveTagsAndBeliefs.put(new Fact("fur(strands,insulator)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("mammal(vertebrate,land)"), 100.0);
//        expectedActiveTagsAndBeliefs.put(new Fact("cat(feline,length>50,weight>20)"), 100.0);
//        expectedActiveTagsAndBeliefs.put(new Fact("fish(vertebrate,water)"), 70.0);
//        expectedActiveTagsAndBeliefs.put(new Fact("teeth(grind,food)"), 100.0);

        System.out.println("[KNN] Inputs from Neural Network: " + inputs.toString());
        System.out.println("[KNN] String trying to find out a link with: " + item);
        System.out.println("[KNN] Active tags after lambda searching: " + activeTags);

        assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        for (Tag t : activeTags) {
            assertEquals(knn.getKnowledgeNode(t).belief, 0d);
        }
        assertEquals(knn.getKnowledgeNode(factToSearch).belief, 0d);
        System.out.println("");

    }
}