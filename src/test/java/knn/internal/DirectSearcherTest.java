package knn.internal;

import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class DirectSearcherTest {
    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;
    private DirectSearcher directSearcher;
    private TreeSet<KnowledgeNode> ageSortedKNs;

    @BeforeMethod
    public void setUp() throws Exception {
        mapKN = new HashMap<>();
        activeTags = new HashSet<>();
        ageSortedKNs = new TreeSet<>();
        directSearcher = new DirectSearcher(mapKN, activeTags, ageSortedKNs);
    }

    @Test
    public void mustDirectSearch() throws Exception {
        Tag inputTag = mock(Tag.class);
        Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        KnowledgeNode kn = mock(KnowledgeNode.class);


        // given
        mapKN.put(inputTag, kn);
        when(kn.getOutputTags()).thenReturn(outputTags);
        when(kn.excite()).thenReturn(true);

        // when
        Set<Tag> activatedTags = directSearcher.search(inputTag);

        // then
        assertEquals(activatedTags, outputTags);
        assertTrue(activeTags.contains(inputTag));
        assertTrue(activeTags.containsAll(outputTags));
    }
}