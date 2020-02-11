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


public class ActivationEnergyTest {

    @Test
    public static void main()throws Exception{
        System.out.println("**Test KnowledgeNode API-- Activation Energy**");
        KnowledgeNode kn_test = new KnowledgeNode("@x");

        if ((kn_test.activation * kn_test.strength) >= kn_test.threshold) {
            System.out.println("***Test passed***");
        }
        else {
            System.out.println("**Test not passed***");
        }
    }

}
