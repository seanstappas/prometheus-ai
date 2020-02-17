package knn.internal;

import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeParseException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertTrue;

public class KnowledgeNodeAgingTest {
    private static final String PET_DATA_PATH = "data/petData.txt";
    private KnowledgeNodeNetwork knn;

    @BeforeMethod
    public void setupKNN() throws KnowledgeNodeParseException {
        knn = null;
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();

        knn.loadData(PET_DATA_PATH);
        String[] info1 = {"Tiger(carnivore,length>50,weight>90)"};
        String[] info2 = {"@isTiger(danger,run)"};
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)"};
        knn.addKnowledgeNode(new KnowledgeNode(info1));
        knn.addKnowledgeNode(new KnowledgeNode(info2));
        knn.addKnowledgeNode(new KnowledgeNode(info3));

        HashMap<Tag, Double> expectedInputTags = new HashMap<>();
        expectedInputTags.put(new Fact("Tiger(carnivore,length>50,weight>90)"), 100.0);
        expectedInputTags.put(new Recommendation("@isTiger(danger,run)"), 100.0);
        expectedInputTags.put(new Rule("friend(nice,kind) -> @meet(community,people>2)"), 100.0);
        expectedInputTags.put(new Fact("apple()"), 0.0);
    }

    //TODO: Test boundary values once aging is refactored
    @Test
    public void mustNotExpire() throws Exception {
        final KnowledgeNode kn = new KnowledgeNode("P(A); P(B)");
        knn.addKnowledgeNode(kn);

        // given
        for(int i=0; i < KnowledgeNode.getAgeTreshold() -2; i++) {
            knn.backwardThink(5);
        }

        // then
        assertTrue(kn.getCurrentAge() < KnowledgeNode.getAgeTreshold() );

        System.out.println("\n**Testing Knowledge Node Aging**");
        System.out.println("\n**Node must not expire**");
    }

    @Test
    public void mustExpire() throws Exception {
        final KnowledgeNode kn = new KnowledgeNode("P(A); P(B)");
        knn.addKnowledgeNode(kn);

        // given
        for(int i=0; i < KnowledgeNode.getAgeTreshold() + 5; i++) {
            knn.backwardThink(5);
        }

        // then
        assertTrue(kn.getCurrentAge() > KnowledgeNode.getAgeTreshold() );

        System.out.println("\n**Testing Knowledge Node Aging**");
        System.out.println("\n**Node must expire**");
    }

    @Test
    public void mustNotExpireWhenReachesFormerExpiration() throws Exception {
        final KnowledgeNode kn = new KnowledgeNode("P(A); P(B)");
        knn.addKnowledgeNode(kn);

        // given
        for(int i=0; i < KnowledgeNode.getAgeTreshold() + 10; i++) {
            if (i == (KnowledgeNode.getAgeTreshold() +1)/2 ) {
                kn.excite();}
            knn.backwardThink(5);
        }

        // then
        assertTrue(kn.getCurrentAge() < KnowledgeNode.getAgeTreshold() );

        System.out.println("\n**Testing Knowledge Node Aging**");
        System.out.println("\n**Node must not expire if the updated node reaches the former expiration **");
    }
}