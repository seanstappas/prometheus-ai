package integration;

import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashMap;

public class KnowledgeNodeNetworkPetTest {
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
    public void testResetEmpty() throws Exception {
        knn.resetEmpty();
        Assert.assertTrue(knn.getActiveTags().isEmpty());
    }

    @Test
    public void createKNFromTupleTest(){
        HashMap<Tag, Double> expectedInputTags = new HashMap<>();
        expectedInputTags.put(new Fact("monkey(intelligent,length>50,weight>3)"), 0.0);
        expectedInputTags.put(new Recommendation("@isAnimal(calm,bark)"), 0.0);
        expectedInputTags.put(new Rule("friend(nice,kind) -> @meet(community,people>2)"), 0.0);
        expectedInputTags.put(new Fact("chair()"), 0.0);
        Assert.assertTrue(knn.getActiveTags().isEmpty());
        System.out.println("");
    }

    @Test
    public void getInputForForwardSearchTest() throws Exception{
        String[] info1 = {"monkey(intelligent,length>50,weight>3)"};
        String[] info2 = {"@isAnimal(calm,bark)"};
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)"};
        knn.addKnowledgeNode(new KnowledgeNode(info1));
        knn.addKnowledgeNode(new KnowledgeNode(info2));
        knn.addKnowledgeNode(new KnowledgeNode(info3));

        HashMap<Tag, Double> expectedInputTags = new HashMap<>();
        expectedInputTags.put(new Fact("monkey(intelligent,length>50,weight>3)"), 100.0);
        expectedInputTags.put(new Recommendation("@isAnimal(calm,bark)"), 100.0);
        expectedInputTags.put(new Rule("friend(nice,kind) -> @meet(community,people>2)"), 100.0);
        expectedInputTags.put(new Fact("banana()"), 0.0);
        System.out.println("[getInputForForwardSearchTest] Output from NN: monkey, @isAnimal");
        System.out.println("[getInputForForwardSearchTest] A wanted rule from the Meta: friend(nice,kind) -> @meet(community,people>2)");
        System.out.println("");
    }

    @Test
    public void getInputForBackwardSearchTest() throws Exception {
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
        System.out.println("[getInputForBackwardSearchTest] Output from NN: monkey, @isAnimal");
        System.out.println("[getInputForBackwardSearchTest] A wanted rule from the Meta: friend(nice,kind) -> @meet(community,people>2)");
        System.out.println("");
    }

    @Test
    public void knToStringTest()  throws Exception{
        String[] info1 = {"Tiger(carnivore,length>50,weight>90)", "monkey(intelligent,length>50,weight>3)"};
        KnowledgeNode kn1 = new KnowledgeNode(info1);
        String[] info2 = {"@isTiger(danger,run)"};
        KnowledgeNode kn2 = new KnowledgeNode(info2);
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)"};
        KnowledgeNode kn3 = new KnowledgeNode(info3);
        System.out.println("[knToStringTest]: "+ kn1.toString());
        System.out.println("[knToStringTest]: "+ kn2.toString());
        System.out.println("[knToStringTest]: "+ kn3.toString());
    }
}