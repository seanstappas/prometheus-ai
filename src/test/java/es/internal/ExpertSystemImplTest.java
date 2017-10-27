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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ExpertSystemImplTest {
    private final static int NUM_TEST_CYCLES = 5;
    private final static String TEST_SENTENCE = "Test sentence.";
    private ExpertSystem es;
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;
    private Thinker thinker;
    private Teacher teacher;
    private Rester rester;

    @BeforeMethod
    public void setUp() throws Exception {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activeRules = new HashSet<>();
        thinker = mock(Thinker.class);
        teacher = mock(Teacher.class);
        rester = mock(Rester.class);
        ThinkerFactory thinkerFactory = mock(ThinkerFactory.class);
        when(thinkerFactory.create(readyRules, activeRules, facts, recommendations)).thenReturn(thinker);
        TeacherFactory teacherFactory = mock(TeacherFactory.class);
        when(teacherFactory.create(readyRules)).thenReturn(teacher);
        ResterFactory resterFactory = mock(ResterFactory.class);
        when(resterFactory.create(readyRules)).thenReturn(rester);
        es = new ExpertSystemImpl(readyRules, activeRules, facts, recommendations, thinkerFactory, teacherFactory, resterFactory);
    }

    @Test
    public void mustThink() throws Exception {
        Set<Recommendation> expected = new HashSet<>();
        expected.add(mock(Recommendation.class));

        // given
        when(thinker.think(false, Integer.MAX_VALUE)).thenReturn(expected);

        // when
        Set<Recommendation> actual = es.think();

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void mustThinkWithoutRuleGeneration() throws Exception {
        Set<Recommendation> expected = new HashSet<>();
        expected.add(mock(Recommendation.class));

        // given
        when(thinker.think(false, Integer.MAX_VALUE)).thenReturn(expected);

        // when
        Set<Recommendation> actual = es.think(false);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void mustThinkWithRuleGeneration() throws Exception {
        Set<Recommendation> expected = new HashSet<>();
        expected.add(mock(Recommendation.class));

        // given
        when(thinker.think(true, Integer.MAX_VALUE)).thenReturn(expected);

        // when
        Set<Recommendation> actual = es.think(true);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void mustThinkWithRuleGenerationAndCycles() throws Exception {
        Set<Recommendation> expected = new HashSet<>();
        expected.add(mock(Recommendation.class));

        // given
        when(thinker.think(true, NUM_TEST_CYCLES)).thenReturn(expected);

        // when
        Set<Recommendation> actual = es.think(true, NUM_TEST_CYCLES);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void testTeach() throws Exception {
        // given
        when(teacher.teach(TEST_SENTENCE)).thenReturn(true);

        // when
        boolean actualTeachResult = es.teach(TEST_SENTENCE);

        // then
        assertTrue(actualTeachResult);
    }

    @Test
    public void testRest() throws Exception {
        // given
        when(rester.rest(NUM_TEST_CYCLES)).thenReturn(true);

        // when
        boolean actualRestResult = es.rest(NUM_TEST_CYCLES);

        // then
        assertTrue(actualRestResult);
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
    public void mustNotAddInvalidTag() throws Exception {
        Tag tag = mock(Tag.class);

        // given
        when(tag.getType()).thenReturn(Tag.TagType.EMPTY);

        // when
        boolean actualAddResult = es.addTag(tag);

        // then
        assertFalse(actualAddResult);
    }

    @Test
    public void mustAddRuleTag() throws Exception {
        Rule rule = mock(Rule.class);

        // given
        when(rule.getType()).thenReturn(Tag.TagType.RULE);

        // when
        es.addTag(rule);

        // then
        assertTrue(readyRules.contains(rule));
        assertTrue(activeRules.isEmpty());
    }

    @Test
    public void mustAddFactTag() throws Exception {
        Fact fact = mock(Fact.class);

        // given
        when(fact.getType()).thenReturn(Tag.TagType.FACT);

        // when
        es.addTag(fact);

        // then
        assertTrue(facts.contains(fact));
    }

    @Test
    public void mustAddRecommendationTag() throws Exception {
        Recommendation recommendation = mock(Recommendation.class);

        // given
        when(recommendation.getType()).thenReturn(Tag.TagType.RECOMMENDATION);

        // when
        es.addTag(recommendation);

        // then
        assertTrue(recommendations.contains(recommendation));
    }

    @Test
    public void mustAddRule() throws Exception {
        Rule rule = mock(Rule.class);

        // given
        when(rule.getType()).thenReturn(Tag.TagType.RULE);

        // when
        es.addReadyRule(rule);

        // then
        assertTrue(readyRules.contains(rule));
        assertTrue(activeRules.isEmpty());
    }

    @Test
    public void mustAddFact() throws Exception {
        Fact fact = mock(Fact.class);

        // given
        when(fact.getType()).thenReturn(Tag.TagType.FACT);

        // when
        es.addFact(fact);

        // then
        assertTrue(facts.contains(fact));
    }

    @Test
    public void mustAddRecommendation() throws Exception {
        Recommendation recommendation = mock(Recommendation.class);

        // given
        when(recommendation.getType()).thenReturn(Tag.TagType.RECOMMENDATION);

        // when
        es.addRecommendation(recommendation);

        // then
        assertTrue(recommendations.contains(recommendation));
    }

    @Test
    public void mustRemoveFact() throws Exception {
        Fact fact = mock(Fact.class);

        // given
        facts.add(fact);

        // when
        es.removeFact(fact);

        // then
        assertTrue(facts.isEmpty());
    }

    @Test
    public void mustGetRecommendations() throws Exception {
        // given
        recommendations.add(mock(Recommendation.class));

        // when
        Set<Recommendation> actualRecommendations = es.getRecommendations();

        // then
        assertEquals(actualRecommendations, recommendations);
    }

    @Test
    public void mustGetReadyRules() throws Exception {
        // given
        readyRules.add(mock(Rule.class));

        // when
        Set<Rule> actualReadyRules = es.getReadyRules();

        // then
        assertEquals(actualReadyRules, readyRules);
    }

    @Test
    public void mustGetActiveRules() throws Exception {
        // given
        activeRules.add(mock(Rule.class));

        // when
        Set<Rule> actualActiveRules = es.getActiveRules();

        // then
        assertEquals(actualActiveRules, activeRules);
    }

    @Test
    public void mustGetFacts() throws Exception {
        // given
        facts.add(mock(Fact.class));

        // when
        Set<Fact> actualFacts = es.getFacts();

        // then
        assertEquals(actualFacts, facts);
    }
}