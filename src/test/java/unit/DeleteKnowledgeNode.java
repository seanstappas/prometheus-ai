package unit;
import static java.lang.System.*;

import com.google.inject.Guice;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeParseException;
import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;


public class DeleteKnowledgeNode {

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



    @Test(expectedExceptions =  KnowledgeNodeParseException.class)
    public void mustFailParsingKn() throws Exception{
        System.out.println("**Test KnowledgeNode API-- delete existing node**");
        new KnowledgeNode("x");
        tags.Tag t1 = new Fact("P(A)");
        knn.deleteKnowledgeNode(t1);
    }

}
