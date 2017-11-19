package knn.internal;

import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;


public class KnowledgeNodeNetworkImplTest {
    private static final double BACKWARD_SEARCH_PARTIAL_MATCH_RATIO = 0.5;
    private KnowledgeNodeNetwork knn;
    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;
    private DirectSearcher directSearcher;
    private ForwardSearcher forwardSearcher;
    private BackwardSearcher backwardSearcher;
    private LambdaSearcher lambdaSearcher;

    @BeforeMethod
    public void setUp() throws Exception {
        mapKN = new HashMap<>();
        activeTags = new HashSet<>();
        directSearcher = mock(DirectSearcher.class);
        forwardSearcher = mock(ForwardSearcher.class);
        backwardSearcher = mock(BackwardSearcher.class);
        lambdaSearcher = mock(LambdaSearcher.class);
        DirectSearcherFactory directSearcherFactory = mock(DirectSearcherFactory.class);
        ForwardSearcherFactory forwardSearcherFactory = mock(ForwardSearcherFactory.class);
        BackwardSearcherFactory backwardSearcherFactory = mock(BackwardSearcherFactory.class);
        LambdaSearcherFactory lambdaSearcherFactory = mock(LambdaSearcherFactory.class);
        when(directSearcherFactory.create(mapKN, activeTags)).thenReturn(directSearcher);
        when(forwardSearcherFactory.create(directSearcher)).thenReturn(forwardSearcher);
        when(backwardSearcherFactory.create(mapKN, activeTags, BACKWARD_SEARCH_PARTIAL_MATCH_RATIO))
                .thenReturn(backwardSearcher);
        when(lambdaSearcherFactory.create(forwardSearcher, backwardSearcher)).thenReturn(lambdaSearcher);
        knn = new KnowledgeNodeNetworkImpl(
                mapKN,
                activeTags,
                BACKWARD_SEARCH_PARTIAL_MATCH_RATIO,
                directSearcherFactory,
                forwardSearcherFactory,
                backwardSearcherFactory,
                lambdaSearcherFactory);
    }

    @Test
    public void mustDirectSearch() throws Exception {
        Tag inputTag = mock(Tag.class);
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));

        // given
        when(directSearcher.search(inputTag)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.directSearch(inputTag);

        // then
        assertEquals(outputTags, activatedTags);
    }

}