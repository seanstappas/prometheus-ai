package integration;

import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import knn.api.Tuple;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KnowledgeNodeNetworkPetTest {
    private static final String PET_DATA_PATH = "data/petData.txt";
    private KnowledgeNodeNetwork knn;
    private List<KnowledgeNode> knowledgeNodes = new ArrayList<>();

    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
    }

    @BeforeMethod
    public void setupKNN(){
        knn.loadData(PET_DATA_PATH, knowledgeNodes);
    }

    @Test
    public void testResetEmpty() throws Exception {
        knn.resetEmpty();
        Assert.assertTrue(knn.getActiveTags().isEmpty());
    }

    @Test
    public void createKNFromTupleTest(){
        Tuple tp1 = new Tuple("monkey(intelligent,length>50,weight>3)", 10);
        Tuple tp2 = new Tuple("@isAnimal(calm,bark)", 10);
        Tuple tp3 = new Tuple("friend(nice,kind) -> @meet(community,people>2)", 10);
        Tuple tp4 = new Tuple("chair", 10);

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
        Tuple tp1 = new Tuple("monkey", 10);
        Tuple tp2 = new Tuple("isAnimal", 10);
        Tuple tp3 = new Tuple("{ [[friend([nice, kind]) 100.0% ]]=>[[@meet([community, people > 2]) 100.0% ]]100.0% }", 10);
        Tuple tp4 = new Tuple("banana", 10);
        ArrayList<Tuple> NNoutputs = new ArrayList<>();
        NNoutputs.add(tp1);
        NNoutputs.add(tp2);
        NNoutputs.add(tp3);
        NNoutputs.add(tp4);

        String[] info1 = {"monkey(intelligent,length>50,weight>3)", "100"};
        String[] info2 = {"@isAnimal(calm,bark)", "100"};
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)", "100"};
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
        Tuple tp1 = new Tuple("Tiger", 10);
        Tuple tp2 = new Tuple("isTiger", 10);
        Tuple tp3 = new Tuple("{ [[friend([nice, kind]) 100.0% ]]=>[[@meet([community, people > 2]) 100.0% ]]100.0% }", 10);
        Tuple tp4 = new Tuple("apple", 10);
        ArrayList<Tuple> NNoutputs = new ArrayList<>();
        NNoutputs.add(tp1);
        NNoutputs.add(tp2);
        NNoutputs.add(tp3);
        NNoutputs.add(tp4);

        String[] info1 = {"Tiger(carnivore,length>50,weight>90)", "100"};
        String[] info2 = {"@isTiger(danger,run)", "100"};
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)", "100"};
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
        String[] info1 = {"Tiger(carnivore,length>50,weight>90)", "100", "monkey(intelligent,length>50,weight>3)", "100"};
        KnowledgeNode kn1 = new KnowledgeNode(info1);
        String[] info2 = {"@isTiger(danger,run)", "100"};
        KnowledgeNode kn2 = new KnowledgeNode(info2);
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)", "100"};
        KnowledgeNode kn3 = new KnowledgeNode(info3);
        System.out.println("[knToStringTest]: "+ kn1.toString());
        System.out.println("[knToStringTest]: "+ kn2.toString());
        System.out.println("[knToStringTest]: "+ kn3.toString());
    }
}