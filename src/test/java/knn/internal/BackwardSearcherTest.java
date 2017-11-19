package knn.internal;

import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertTrue;

public class BackwardSearcherTest {
    private BackwardSearcher backwardSearcher;
    private Map<Tag, KnowledgeNode> mapKN;
    private BackwardSearchMatcher backwardSearchMatcher;

    @BeforeMethod
    public void setUp() throws Exception {
        mapKN = new HashMap<>();
        Set<Tag> activeTags = new HashSet<>();
        backwardSearchMatcher = mock(BackwardSearchMatcher.class);
        double partialMatchRatio = 0.5;
        backwardSearcher = new BackwardSearcher(mapKN, activeTags, partialMatchRatio, backwardSearchMatcher);
    }

    @Test
    public void mustBackwardSearch() throws Exception {
        Set<Tag> inputTags = new HashSet<>(Arrays.asList(
                mock(Tag.class), mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        int ply = 5;
        int numRequiredMatches = 2;
        KnowledgeNode kn = mock(KnowledgeNode.class);
        Set<Tag> backwardSearchMatcherTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class)));

        // given
        mapKN.put(mock(Tag.class), kn);
        when(backwardSearchMatcher.match(inputTags, kn, numRequiredMatches)).thenReturn(backwardSearchMatcherTags);

        // when
        Set<Tag> allActivatedTags = backwardSearcher.search(inputTags, ply);

        // then
        assertTrue(allActivatedTags.containsAll(inputTags));
        assertTrue(allActivatedTags.containsAll(backwardSearchMatcherTags));
    }
}