package knn.internal;

import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class BackwardSearchMatcherTest {
  BackwardSearchMatcher backwardSearchMatcher;

  @BeforeMethod
  public void setUp() throws Exception {
    backwardSearchMatcher = new BackwardSearchMatcher();
  }

  @Test
  public void mustMatch() throws Exception {
    Tag t1 = mock(Tag.class);
    Tag t2 = mock(Tag.class);
    Tag t3 = mock(Tag.class);
    Tag t4 = mock(Tag.class);
    Set<Tag> inputTags = new HashSet<>(Arrays.asList(t1, t2, t3));
    KnowledgeNode kn = mock(KnowledgeNode.class);
    Tag inputTag = mock(Tag.class);
    Set<Tag> outputTags = new HashSet<>(Arrays.asList(t1, t2, t4));
    int numRequiredMatches = 2;

    // given
    when(kn.getInputTag()).thenReturn(inputTag);
    when(kn.getOutputTags()).thenReturn(outputTags);
    when(kn.excite()).thenReturn(true);

    // when
    Optional<Tag> actualTag = backwardSearchMatcher.match(inputTags, kn, numRequiredMatches);

    // then
    assertEquals(Optional.of(inputTag), actualTag);
  }

  @Test
  public void mustNotMatch() throws Exception {
    Tag t1 = mock(Tag.class);
    Tag t2 = mock(Tag.class);
    Tag t3 = mock(Tag.class);
    Tag t4 = mock(Tag.class);
    Set<Tag> inputTags = new HashSet<>(Arrays.asList(t1, t2, t3));
    KnowledgeNode kn = mock(KnowledgeNode.class);
    Tag inputTag = mock(Tag.class);
    Set<Tag> outputTags = new HashSet<>(Arrays.asList(t1, t2, t4));
    int numRequiredMatches = 3;

    // given
    when(kn.getInputTag()).thenReturn(inputTag);
    when(kn.getOutputTags()).thenReturn(outputTags);
    when(kn.excite()).thenReturn(true);

    // when
    Optional<Tag> actualTag = backwardSearchMatcher.match(inputTags, kn, numRequiredMatches);

    // then
    assertEquals(Optional.empty(), actualTag);
  }
}