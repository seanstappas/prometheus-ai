package unit;

import com.google.inject.Guice;
import knn.api.KnowledgeNodeNetwork;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Tag;

import java.util.HashMap;

public class ConfidenceTest {
    private static final String PET_DATA_PATH = "data/petData.txt";
    private KnowledgeNodeNetwork knn;

    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
    }

    @BeforeMethod
    public void setupKNN(){
        knn.loadData(PET_DATA_PATH);
    }

    @Test
    public void testConfidence() throws Exception {
        Recommendation rec1 = new Recommendation("@isAnimal(calm,bark)", 100.0);
        Recommendation rec2 = new Recommendation("@isTiger(danger,run)", 0.0);

        double conf1 = rec1.getConfidence();
        double conf2 = rec2.getConfidence();

        if (conf1 > conf2) {
            System.out.println("Recommendation 1 is more likely");
        }
        else {
            System.out.println("Recommendation 2 is more likely");
        }


    }
}
