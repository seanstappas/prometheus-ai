package es.internal;

import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

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
    public void mustResetRulesFactsAndRecommendations() throws Exception {
        // given
        readyRules.add(mock(Rule.class));
        activeRules.add(mock(Rule.class));
        facts.add(mock(Fact.class));
        recommendations.add(mock(Recommendation.class));

        // when
        es.reset();

        // then
        assertTrue(readyRules.isEmpty());
        assertTrue(activeRules.isEmpty());
        assertTrue(facts.isEmpty());
        assertTrue(recommendations.isEmpty());
    }

    @Test
    public void mustDeactivateRules() throws Exception {
        // given
        Rule readyRule = mock(Rule.class);
        Rule activeRule = mock(Rule.class);
        readyRules.add(readyRule);
        activeRules.add(activeRule);

        // when
        es.deactivateRules();

        // then
        assertTrue(readyRules.contains(readyRule));
        assertTrue(readyRules.contains(activeRule));
        assertTrue(activeRules.isEmpty());
    }

    @Test
    public void mustAddTags() throws Exception {
        Set<Tag> tags = new HashSet<>();
        Rule rule = mock(Rule.class);
        Fact fact = mock(Fact.class);
        Recommendation recommendation = mock(Recommendation.class);
        tags.add(rule);
        tags.add(fact);
        tags.add(recommendation);

        // given
        when(rule.getType()).thenReturn(Tag.TagType.RULE);
        when(fact.getType()).thenReturn(Tag.TagType.FACT);
        when(recommendation.getType()).thenReturn(Tag.TagType.RECOMMENDATION);

        // when
        es.addTags(tags);

        // then
        assertTrue(readyRules.contains(rule));
        assertTrue(activeRules.isEmpty());
        assertTrue(facts.contains(fact));
        assertTrue(recommendations.contains(recommendation));
    }

    @Test
    public void mustAddTag() throws Exception {

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