package es.internal;

import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

/**
 * Created by seanstappas1 on 2017-10-13.
 */
public class ExpertSystemImplTest {
    private ExpertSystem es;
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;

    @BeforeMethod
    public void setUp() throws Exception {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activeRules = new HashSet<>();
        es = new ExpertSystemImpl(readyRules, activeRules, facts, recommendations);
    }

    @Test
    public void testReset() throws Exception {
        es.reset();

        assertTrue(es.getActiveRules().isEmpty());
        assertTrue(es.getReadyRules().isEmpty());
        assertTrue(es.getFacts().isEmpty());
        assertTrue(es.getRecommendations().isEmpty());
    }

    @Test
    public void testDeactivateRules() throws Exception {
        Rule rule = mock(Rule.class);

        es.addRule(rule);
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