package knn.internal;

import com.google.inject.Guice;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeParseException;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class ForwardSearchIntegrationTest {
    private static final String PET_DATA_PATH = "data/petData.txt";
    private KnowledgeNodeNetwork knn;
    private DirectSearcher directSearcher;
    private ForwardSearcher forwardSearcher;


    @BeforeTest
    public void setup() {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
        directSearcher = mock(DirectSearcher.class);
        forwardSearcher = new ForwardSearcher(directSearcher);
    }

    @Test
    public void setupKNN() throws KnowledgeNodeParseException {
        knn.loadData(PET_DATA_PATH);
        String[] info1 = {"Tiger(carnivore,length>50,weight>90)"};
        String[] info2 = {"@isTiger(danger,run)"};
        String[] info3 = {"friend(nice,kind) -> @meet(community,people>2)"};
        knn.addKnowledgeNode(new KnowledgeNode(info1));
        knn.addKnowledgeNode(new KnowledgeNode(info2));
        knn.addKnowledgeNode(new KnowledgeNode(info3));

        HashMap<Tag, Double> expectedInputTags = new HashMap<>();
        Tag t1 = new Fact("Tiger(carnivore,length>50,weight>90)",100);
        Tag t2 = new Recommendation("@isTiger(danger,run)",100.0);
        Tag t3 = new Rule("friend(nice,kind) -> @meet(community,people>2)");
        Tag t4 = new Fact("apple()", 0.0);

        Set<Tag> inputTags = new HashSet<>(Arrays.asList(t1, t2));
        Set<Tag> directActivatedTags1 = new HashSet<>(Collections.singletonList(t3));
        Set<Tag> directActivatedTags2 = new HashSet<>(Collections.singletonList(t4));
        int ply = Integer.MAX_VALUE;
        Set<Tag> expectedAllActivatedTags = new HashSet<>(Arrays.asList(t3, t4));

        // given
        when(directSearcher.search(t1)).thenReturn(directActivatedTags1);
        when(directSearcher.search(t2)).thenReturn(directActivatedTags2);
        when(directSearcher.search(t3)).thenReturn(Collections.emptySet());
        when(directSearcher.search(t4)).thenReturn(Collections.emptySet());


        Set<Tag> actualAllActivatedTags = forwardSearcher.searchInternal(inputTags, ply);

        System.out.println(actualAllActivatedTags);

        // then
        assertEquals(expectedAllActivatedTags, actualAllActivatedTags);
        verify(directSearcher, times(4)).search(any(Tag.class));
        System.out.println("**Test Froward Search with Ply test");

    }


}
