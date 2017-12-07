package knn.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;

public class BackwardSearchMatcherTest {
    BackwardSearchMatcher backwardSearchMatcher;

    @BeforeMethod
    public void setUp() throws Exception {
        backwardSearchMatcher = new BackwardSearchMatcher();
    }

    @Test
    public void mustMatch() throws Exception {
        // given
        final Tag t1 = mock(Tag.class);
        final Tag t2 = mock(Tag.class);
        final Tag t3 = mock(Tag.class);
        final Tag t4 = mock(Tag.class);
        final Set<Tag> inputTags = new HashSet<>(Arrays.asList(t1, t2, t3));
        final Tag inputTag = mock(Tag.class);
        final Set<Tag> outputTags = new HashSet<>(Arrays.asList(t1, t2, t4));
        final KnowledgeNode kn = new KnowledgeNode(inputTag, outputTags, 1);
        final int numRequiredMatches = 2;

        // when
        final Optional<Tag> actualTag = backwardSearchMatcher.match(inputTags, kn, numRequiredMatches);

        // then
        assertEquals(Optional.of(inputTag), actualTag);
    }

    @Test
    public void mustNotMatch() throws Exception {
        // given
        final Tag t1 = mock(Tag.class);
        final Tag t2 = mock(Tag.class);
        final Tag t3 = mock(Tag.class);
        final Tag t4 = mock(Tag.class);
        final Set<Tag> inputTags = new HashSet<>(Arrays.asList(t1, t2, t3));
        final Tag inputTag = mock(Tag.class);
        final Set<Tag> outputTags = new HashSet<>(Arrays.asList(t1, t2, t4));
        final KnowledgeNode kn = new KnowledgeNode(inputTag, outputTags, 1);
        final int numRequiredMatches = 3;

        // when
        final Optional<Tag> actualTag = backwardSearchMatcher.match(inputTags, kn, numRequiredMatches);

        // then
        assertEquals(Optional.empty(), actualTag);
    }
}