package test;

import knn.KnowledgeNodeNetwork;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Knowledge Node Network Unit Tests
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

    @Test
    public void testReset() throws Exception {
        knn.reset("test1");
        Assert.assertTrue(knn.getActiveTags().isEmpty());
    }

    @Test
    public void testResetEmpty() throws Exception {
        knn.resetEmpty();
        Assert.assertTrue(knn.getActiveTags().isEmpty());
    }

    @Test
    public void testSaveKNN() throws Exception {
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