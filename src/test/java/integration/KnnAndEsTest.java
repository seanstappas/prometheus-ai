package integration;

import com.google.inject.Guice;
import es.api.ExpertSystem;
import knn.api.KnowledgeNodeNetwork;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
        knn.loadData(ANIMAL_DATA_PATH);
    }

    /**
     * Tests the ES and KNN together.
     */
    @Test
    public void testKNNandES() {
        System.out.println();
        System.out.println("testKNNandES");
        setupKNN();
        knn.forwardThink(0);
        Set<Tag> activatedTags = knn.getActiveTags();
        es.reset();
        es.addTags(activatedTags);

        Set<Fact> expectedInitialFacts = new HashSet<>();
        Set<Rule> expectedInitialRules = new HashSet<>();

        for (Tag t : activatedTags) {
            if (t instanceof Fact)
                expectedInitialFacts.add((Fact) t);
            else if (t instanceof Rule)
                expectedInitialRules.add((Rule) t);
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

        assertTrue(activatedRecommendations.isEmpty());
    }
}
