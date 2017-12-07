package knn.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertTrue;

public class BackwardSearcherTest {
    private BackwardSearcher backwardSearcher;
    private BackwardSearchMatcher backwardSearchMatcher;
    private TreeSet<KnowledgeNode> ageSortedKNs;

    @BeforeMethod
    public void setUp() throws Exception {
        final Set<Tag> activeTags = new HashSet<>();
        backwardSearchMatcher = mock(BackwardSearchMatcher.class);
        ageSortedKNs = new TreeSet<>();
        final double partialMatchRatio = 0.5;
        final long ageLimit = Long.MAX_VALUE;
        backwardSearcher = new BackwardSearcher(activeTags, ageSortedKNs, partialMatchRatio, ageLimit, backwardSearchMatcher);
    }

    @Test
    public void mustBackwardSearch() throws Exception {
        final Set<Tag> inputTags = new HashSet<>(Arrays.asList(
                mock(Tag.class), mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        final int ply = 5;
        final int numRequiredMatches = 2;
        final KnowledgeNode kn = new KnowledgeNode("P(A); P(B)");
        final Tag backwardSearchMatcherTag = mock(Tag.class);

        // given
        ageSortedKNs.add(kn);
        when(backwardSearchMatcher.match(inputTags, kn, numRequiredMatches))
                .thenReturn(Optional.of(backwardSearchMatcherTag));

        // when
        final Set<Tag> allActivatedTags = backwardSearcher.searchInternal(inputTags, ply);

        // then
        assertTrue(allActivatedTags.contains(backwardSearchMatcherTag));
    }
}