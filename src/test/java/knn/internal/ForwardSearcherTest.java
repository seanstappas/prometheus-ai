package knn.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class ForwardSearcherTest {
    private DirectSearcher directSearcher;
    private ForwardSearcher forwardSearcher;

    @BeforeMethod
    public void setUp() throws Exception {
        directSearcher = mock(DirectSearcher.class);
        forwardSearcher = new ForwardSearcher(directSearcher);
    }

    @Test
    public void mustForwardSearch() throws Exception {
        Tag t1 = mock(Tag.class);
        Tag t2 = mock(Tag.class);
        Tag t3 = mock(Tag.class);
        Tag t4 = mock(Tag.class);
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

        // when
        Set<Tag> actualAllActivatedTags = forwardSearcher.searchInternal(inputTags, ply);

        // then
        assertEquals(expectedAllActivatedTags, actualAllActivatedTags);
        verify(directSearcher, times(4)).search(any(Tag.class));
    }
}