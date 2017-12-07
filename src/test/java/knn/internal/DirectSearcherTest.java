package knn.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import static org.mockito.Mockito.mock;
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
        final Tag inputTag = mock(Tag.class);
        final Set<Tag> outputTags = new HashSet<>(Arrays.asList(mock(Tag.class), mock(Tag.class), mock(Tag.class)));
        final KnowledgeNode kn = new KnowledgeNode(inputTag, outputTags, 1);


        // given
        mapKN.put(inputTag, kn);

        // when
        final Set<Tag> activatedTags = directSearcher.search(inputTag);

        // then
        assertEquals(activatedTags, outputTags);
        assertTrue(activeTags.contains(inputTag));
        assertTrue(activeTags.containsAll(outputTags));
    }
}