package knn.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class LambdaSearcherTest {
    private LambdaSearcher lambdaSearcher;
    private ForwardSearcher forwardSearcher;
    private BackwardSearcher backwardSearcher;

    @BeforeMethod
    public void setUp() throws Exception {
        forwardSearcher = mock(ForwardSearcher.class);
        backwardSearcher = mock(BackwardSearcher.class);
        lambdaSearcher = new LambdaSearcher(forwardSearcher, backwardSearcher);
    }

    @Test
    public void mustLambdaSearch() throws Exception {
        Set<Tag> inputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class)));
        Set<Tag> backwardTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class)));
        Set<Tag> forwardTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class)));
        int ply = 6;

        // given
        when(backwardSearcher.search(inputTags, ply)).thenReturn(backwardTags);
        when(forwardSearcher.search(backwardTags, ply)).thenReturn(forwardTags);

        // when
        Set<Tag> activatedTags = lambdaSearcher.searchInternal(inputTags, ply);

        // then
        assertEquals(forwardTags, activatedTags);
    }
}