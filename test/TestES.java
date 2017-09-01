package test;

import es.ExpertSystem;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Expert System Unit Tests
 */
public class TestES {
    private ExpertSystem es;

    @BeforeMethod
    public void setUp() throws Exception {
        es = new ExpertSystem();
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testReset() throws Exception {
        es.reset();
        Assert.assertTrue(es.getActiveRules().isEmpty());
        Assert.assertTrue(es.getFacts().isEmpty());
        Assert.assertTrue(es.getReadyRules().isEmpty());
        Assert.assertTrue(es.getRecommendations().isEmpty());
    }

    @Test
    public void testAddTag() throws Exception {
    }

    @Test
    public void testAddTags() throws Exception {
    }

    @Test
    public void testAddFact() throws Exception {
    }

    @Test
    public void testAddRule() throws Exception {
    }

    @Test
    public void testAddRecommendation() throws Exception {
    }

    @Test
    public void testGetRecommendations() throws Exception {
    }

    @Test
    public void testGetReadyRules() throws Exception {
    }

    @Test
    public void testGetActiveRules() throws Exception {
    }

    @Test
    public void testGetFacts() throws Exception {
    }

    @Test
    public void testThink() throws Exception {
        es.think();
    }

    @Test
    public void testThinkNumCycles() throws Exception {
        es.think(0, true);
    }

}