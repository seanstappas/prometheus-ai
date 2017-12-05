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
    private TreeSet<KnowledgeNode> ageSortedKNs;
    private DirectSearcher directSearcher;
    private ForwardSearcher forwardSearcher;
    private BackwardSearcher backwardSearcher;
    private LambdaSearcher lambdaSearcher;

    @BeforeMethod
    public void setUp() throws Exception {
        mapKN = new HashMap<>();
        activeTags = new HashSet<>();
        ageSortedKNs = new TreeSet<>();
        directSearcher = mock(DirectSearcher.class);
        forwardSearcher = mock(ForwardSearcher.class);
        backwardSearcher = mock(BackwardSearcher.class);
        lambdaSearcher = mock(LambdaSearcher.class);
        DirectSearcherFactory directSearcherFactory = mock(DirectSearcherFactory.class);
        ForwardSearcherFactory forwardSearcherFactory = mock(ForwardSearcherFactory.class);
        BackwardSearcherFactory backwardSearcherFactory = mock(BackwardSearcherFactory.class);
        LambdaSearcherFactory lambdaSearcherFactory = mock(LambdaSearcherFactory.class);
        when(directSearcherFactory.create(mapKN, activeTags, ageSortedKNs)).thenReturn(directSearcher);
        when(forwardSearcherFactory.create(directSearcher)).thenReturn(forwardSearcher);
        long ageLimit = Long.MAX_VALUE;
        when(backwardSearcherFactory.create(activeTags, ageSortedKNs, BACKWARD_SEARCH_PARTIAL_MATCH_RATIO, ageLimit))
                .thenReturn(backwardSearcher);
        when(lambdaSearcherFactory.create(forwardSearcher, backwardSearcher)).thenReturn(lambdaSearcher);
        knn = new KnowledgeNodeNetworkImpl(
                mapKN,
                activeTags,
                ageSortedKNs,
                BACKWARD_SEARCH_PARTIAL_MATCH_RATIO,
                ageLimit,
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
        Set<Tag> inputTags = new HashSet<>(Collections.singletonList(mock(Tag.class)));
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;

        // given
        when(forwardSearcher.search(inputTags, ply)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.forwardSearch(inputTags, ply);

        // then
        assertEquals(outputTags, activatedTags);
    }

    @Test
    public void mustForwardThink() throws Exception {
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;

        // given
        when(forwardSearcher.search(activeTags, ply)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.forwardThink(ply);

        // then
        assertEquals(outputTags, activatedTags);
    }

    @Test
    public void mustBackwardSearch() throws Exception {
        Set<Tag> inputTags = new HashSet<>(Collections.singletonList(mock(Tag.class)));
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;

        // given
        when(backwardSearcher.search(inputTags, ply)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.backwardSearch(inputTags, ply);

        // then
        assertEquals(outputTags, activatedTags);
    }

    @Test
    public void mustBackwardThink() throws Exception {
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;

        // given
        when(backwardSearcher.search(activeTags, ply)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.backwardThink(ply);

        // then
        assertEquals(outputTags, activatedTags);
    }

    @Test
    public void mustLambdaSearch() throws Exception {
        Set<Tag> inputTags = new HashSet<>(Collections.singletonList(mock(Tag.class)));
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;

        // given
        when(lambdaSearcher.search(inputTags, ply)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.lambdaSearch(inputTags, ply);

        // then
        assertEquals(outputTags, activatedTags);
    }

    @Test
    public void mustLambdaThink() throws Exception {
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;

        // given
        when(lambdaSearcher.search(activeTags, ply)).thenReturn(outputTags);

        // when
        Set<Tag> activatedTags = knn.lambdaThink(ply);

        // then
        assertEquals(outputTags, activatedTags);
    }

}