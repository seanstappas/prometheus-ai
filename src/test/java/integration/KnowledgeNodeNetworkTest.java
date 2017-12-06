package integration;

import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

public class KnowledgeNodeNetworkTest {
    private static final String ANIMAL_DATA_PATH = "data/animalData.txt";
    private KnowledgeNodeNetwork knn;

    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
    }

    @BeforeMethod
    public void setupKNN() {
        knn.loadData(ANIMAL_DATA_PATH);
    }

    @Test
    public void testForwardThink() throws Exception {
        Set<Tag> expectedActivatedTags = new HashSet<>();
        int i = 0;
        for (KnowledgeNode kn : knn.getKnowledgeNodes()) {
            knn.addActiveTag(kn.getInputTag());
            expectedActivatedTags.addAll(kn.getOutputTags());
            if (i == knn.getKnowledgeNodes().size() / 2) {
                break;
            }
            i++;
        }

        Set<Tag> actualActivatedTags = knn.forwardThink(1);

        assertEquals(expectedActivatedTags, actualActivatedTags);
    }
}