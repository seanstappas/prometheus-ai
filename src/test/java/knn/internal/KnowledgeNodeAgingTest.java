package knn.internal;

import knn.api.KnowledgeNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Tag;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertTrue;

public class KnowledgeNodeAgingTest {
    private TreeSet<KnowledgeNode> ageSortedKNs;

    @BeforeMethod
    public void setUp() throws Exception {
        // = new TreeSet<>();
    }


    //TODO: Test boundary values once aging is refactored
    @Test
    public void mustNotExpire() throws Exception {
        final KnowledgeNode kn = new KnowledgeNode("P(A); P(B)");

        // given
        //ageSortedKNs.add(kn);

        // then
        //assertTrue();

        System.out.println("\n**Testing Knowledge Node Aging**");
    }
}