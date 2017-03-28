package test;

import knn.KnowledgeNodeNetwork;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Knowledge Node Network Unit Tests
 * TODO: Complete this
 */
public class TestKNN {
    KnowledgeNodeNetwork knn;

    @BeforeMethod
    public void setUp() throws Exception {
        knn = new KnowledgeNodeNetwork("test");
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @org.testng.annotations.Test
    public void testReset() throws Exception {
    }

    @Test
    public void testResetEmpty() throws Exception {
    }

    @Test
    public void testSaveKNN() throws Exception {
    }

    @Test
    public void testAddTupleNN() throws Exception {
    }

    @Test
    public void testAddMETA() throws Exception {
    }

    @Test
    public void testClearNN() throws Exception {
    }

    @Test
    public void testClearMETA() throws Exception {
    }

    @Test
    public void testClearKN() throws Exception {
    }

    @Test
    public void testAddKN() throws Exception {
    }

    @Test
    public void testDelKN() throws Exception {
    }

    @Test
    public void testAddFiredTag() throws Exception {
    }

    @Test
    public void testGetActiveTags() throws Exception {
    }

    @Test
    public void testThink() throws Exception {
    }

    @Test
    public void testThinkNumCycles() throws Exception {
    }

}