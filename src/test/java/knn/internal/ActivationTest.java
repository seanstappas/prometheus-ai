package knn.internal;

import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeParseException;
import knn.internal.DirectSearcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Rule;
import tags.Tag;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ActivationTest {
    private static final String PET_DATA_PATH = "data/petData.txt";
    private KnowledgeNodeNetwork knn;

    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;
    private DirectSearcher directSearcher;
    private TreeSet<KnowledgeNode> ageSortedKNs;

    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
        mapKN = new HashMap<>();
        activeTags = new HashSet<>();
        ageSortedKNs = new TreeSet<>();
        directSearcher = new DirectSearcher(mapKN, activeTags, ageSortedKNs);
        knn.loadData(PET_DATA_PATH);
    }


    /**
     * Test the direct Search Activation of the Knowledge Node network
     *
     */
    @Test
    public void directSearchActivationTest() throws KnowledgeNodeParseException {
        System.out.println("***Direct Search Activation Test***");


        final Tag inputTag = mock(Tag.class);
        final Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        final KnowledgeNode kn = new KnowledgeNode(inputTag, outputTags, 1);


        // given
        mapKN.put(inputTag, kn);

        // when
        final Set<Tag> activatedTags = directSearcher.search(inputTag);


        //output active nodes, their activation, current threshold

        for (Tag t : activatedTags) {
            /*
            System.out.println(t);
            System.out.println("Node: " + kn);
            System.out.println("Activation: " + kn.activation);
            System.out.println("Threshold: " + kn.threshold);*/

            assertTrue(kn.activation >= kn.threshold, "Activation " + kn.activation + " should be greater than or equal to current threshold " + kn.threshold + " ");

        }

    }

    /**
     * Test the backward Search Activation of the Knowledge Node network
     *

    @Test
    public void backwardSearchActivationTest() {
        System.out.println("***Backward Search Activation Test***");
        Fact fact1 = new Fact("calm(safe>5)");
        Fact fact2 = new Fact("coward(scared,safe)");
        knn.addActiveTags(fact1, fact2);

        knn.setBackwardSearchMatchRatio(0.5);
        knn.backwardThink(0);
        Set<Tag> activeTags = knn.getActiveTags();
        Map<Tag, Double> expectedActiveTagsAndBeliefs = new HashMap<>();
        expectedActiveTagsAndBeliefs.put(fact1, 100.0);
        expectedActiveTagsAndBeliefs.put(fact2, 100.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fast(speed,dynamic)"), 40.0);
        expectedActiveTagsAndBeliefs.put(new Fact("fur(strands,insulator)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("mammal(vertebrate,land)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bird(vertebrate,air)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("feathers(floating,insulator,flight)"), 30.0);
        expectedActiveTagsAndBeliefs.put(new Fact("dog(wolflike,length>50,weight>20)"), 60.0);
        expectedActiveTagsAndBeliefs.put(new Fact("bark(sound,loud)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("teeth(grind,food)"), 80.0);
        expectedActiveTagsAndBeliefs.put(new Fact("cat(feline,length>50,weight>20)"), 80.0);

        //System.out.println("[KNN] Active tags after backward searching: " + activeTags);

        //assertEquals(activeTags, expectedActiveTagsAndBeliefs.keySet());
        /*
        for (Tag t : activeTags) {
            assertTrue(knn.getKnowledgeNode(t).activation <= knn.getKnowledgeNode(t).threshold, "Activation " + knn.getKnowledgeNode(t).activation + " should be greater than or equal to current threshold" + knn.getKnowledgeNode(t).threshold + " ");
        }
        System.out.println("");

    }*/

}
