package integration;

import com.google.inject.Guice;
import es.api.ExpertSystem;
import knn.api.KnowledgeNodeNetwork;
import knn.api.Tuple;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class KnnAndEsTest {
    private static final String ANIMAL_DATA_PATH = "data/animalData.txt";
    private KnowledgeNodeNetwork knn;
    private ExpertSystem es;

    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        es = prometheus.getExpertSystem();
        knn = prometheus.getKnowledgeNodeNetwork();
    }


    /**
     * Sets up a KNN by reading a input data file (in this case called petData.txt.txt)
     */
    @BeforeMethod
    public void setupKNN() {
        KnnDataLoader.loadDate(knn, ANIMAL_DATA_PATH, new ArrayList<>());
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
        Set<Tag> activatedTags = knn.getActiveTags();
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
        assertEquals(initialFacts, expectedInitialFacts);
        System.out.println("[ES] Initial facts (for ES): " + initialFacts);

        Set<Rule> initialRules = es.getReadyRules();
        assertEquals(initialRules, expectedInitialRules);
        System.out.println("[ES] Initial rules (for ES): " + initialRules);

        Set<Recommendation> activatedRecommendations = es.think();
        System.out.println("[ES] Active recommendation (for Meta): " + activatedRecommendations);

        Set<Tag> expectedActivatedRecommendation = new HashSet<>();
        expectedActivatedRecommendation.add(new Recommendation("@avoid(scary,dangerous)"));
        expectedActivatedRecommendation.add(new Recommendation("@isPet(easy,calm,bark)"));
        assertEquals(activatedRecommendations, expectedActivatedRecommendation);
    }
}
