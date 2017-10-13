package es.internal;

import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * Created by seanstappas1 on 2017-10-13.
 */
public class ExpertSystemImplTest {
    private ExpertSystem es;

    @BeforeMethod
    public void setUp() throws Exception {
        es = new ExpertSystemImpl();
    }

    @Test
    public void testReset() throws Exception {
    }

    @Test
    public void testDeactivateRules() throws Exception {
    }

    @Test
    public void testAddTags() throws Exception {
    }

    @Test
    public void testAddFact() throws Exception {
    }

    @Test
    public void testRemoveFact() throws Exception {
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
    }

    @Test
    public void testThink1() throws Exception {
    }

    @Test
    public void testThink2() throws Exception {
    }

    @Test
    public void testRest() throws Exception {
    }

    @Test
    public void testTeach() throws Exception {
    }

}