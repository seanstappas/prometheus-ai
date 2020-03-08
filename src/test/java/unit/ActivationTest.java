package unit;

import com.google.inject.Guice;
import knn.api.KnowledgeNodeNetwork;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Rule;
import tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class ActivationTest {
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
        knn.loadData(ANIMAL_DATA_PATH);
    }

    /**
     * Test the forward Search of the Knowledge Node network
     *
     */
    @Test
    public void forwardSearchActivationTest() {
        System.out.println("***Forward Search Activation Test***");
        Fact fact1 = new Fact("dog(wolflike,length>50,weight>20)");
        Fact fact2 = new Fact("cat(feline,length>50,weight>20)");

        knn.addActiveTags(fact1, fact2);

        knn.forwardThink(0);
        Set<Tag> activeTags = knn.getActiveTags();

        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();


        expectedActiveTagsAndBeliefs.put(new Fact("calm(safe>5)"), 55.0);
        expectedActiveTagsAndBeliefs.put(fact1, 100.0);
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
        expectedActiveTagsAndBeliefs.put(fact2, 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fish(vertebrate,water)"), 50.0);
        expectedActiveTagsAndBeliefs.put(new Fact("pet(scary,attractive)"), 44.0);
        expectedActiveTagsAndBeliefs.put(new Fact("confused(blur,prey)"), 55.0);
        expectedActiveTagsAndBeliefs.put(new Rule("massive(big,heavy) teeth(grind,food) -> @scary(scary,dangerous)"), 100.0);
        expectedActiveTagsAndBeliefs.put(new Rule("enemy(scary,dangerous) -> @avoid(scary,dangerous)"), 40.5);
        expectedActiveTagsAndBeliefs.put(new Rule("coward(scared,safe) loyal(easy,calm) -> @isSafe(easy,calm)"), 24.2);


        assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        for (Tag t : activeTags) {
            if (knn.getKnowledgeNode(t).activation >= knn.getKnowledgeNode(t).threshold){
                System.out.println("**Activation successful for Forward Search**");
            }
        }

    }

}
