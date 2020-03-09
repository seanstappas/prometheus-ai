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
import static org.testng.AssertJUnit.*;

public class BackwardSearchIntegrationTest {
    private static final String PET_DATA_PATH = "data/petData.txt";
    private KnowledgeNodeNetwork knn;
    private BackwardSearchMatcher backwardSearchMatcher;
    private HashMap<Tag, Double> expectedInputTags;

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

        expectedInputTags = new HashMap<>();
        expectedInputTags.put(new Fact("Tiger(carnivore,length>50,weight>90)"), 100.0);
        expectedInputTags.put(new Recommendation("@isTiger(danger,run)"), 100.0);
        expectedInputTags.put(new Rule("friend(nice,kind) -> @meet(community,people>2)"), 100.0);
        expectedInputTags.put(new Fact("apple()"), 0.0);

        backwardSearchMatcher = mock(BackwardSearchMatcher.class);
    }

    @Test
    public void mustBackwardSearch() throws Exception {
        final Tag backwardSearchMatcherTag = mock(Tag.class);
        final Set<Tag> inputTags = new HashSet<>(Arrays.asList(
                backwardSearchMatcherTag, mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        final int ply = 1;
        final int numRequiredMatches = 1;
        final KnowledgeNode kn = new KnowledgeNode(backwardSearchMatcherTag, inputTags, 1);

        // given
        knn.addKnowledgeNode(kn);
        when(backwardSearchMatcher.match(inputTags, kn, numRequiredMatches))
                .thenReturn(Optional.of(backwardSearchMatcherTag));

        // when
        final Set<Tag> allActivatedTags = knn.backwardSearch(inputTags, ply);

        // then
        assertTrue(allActivatedTags.contains(backwardSearchMatcherTag));
    }
}
