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

    @Test
    public void mustForwardSearch() throws Exception {
        Set<Tag> inputTags = new HashSet<>();
    }

    @Test
    public void testReset() throws Exception {
    }

    @Test
    public void testResetEmpty() throws Exception {
    }

    @Test
    public void testSaveKNN() throws Exception {
    }

    @Test
    public void testClearKN() throws Exception {
    }

    @Test
    public void testAddKN() throws Exception {
    }

    @Test
    public void testDelKN() throws Exception {
    }

    @Test
    public void testAddFiredTag() throws Exception {
    }

    @Test
    public void testGetInputTags() throws Exception {
    }

    @Test
    public void testGetActiveTags() throws Exception {
    }

    @Test
    public void testLambdaSearch() throws Exception {
    }

    @Test
    public void testBackwardSearch() throws Exception {
    }

    @Test
    public void testGetInputForBackwardSearch() throws Exception {
    }

    @Test
    public void testBackwardSearch1() throws Exception {
    }

    @Test
    public void testForwardSearch() throws Exception {
    }

    @Test
    public void testForwardSearch1() throws Exception {
    }

    @Test
    public void testGetInputForForwardSearch() throws Exception {
    }

    @Test
    public void testCreateKNfromTuple() throws Exception {
    }

    @Test
    public void testExcite() throws Exception {
    }

    @Test
    public void testFire() throws Exception {
    }

    @Test
    public void testUpdateConfidence() throws Exception {
    }

}