package es.internal;

import java.util.HashSet;
import java.util.Set;
import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

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
        final ThinkerFactory thinkerFactory = mock(ThinkerFactory.class);
        when(thinkerFactory.create(readyRules, activeRules, facts, recommendations)).thenReturn(thinker);
        final TeacherFactory teacherFactory = mock(TeacherFactory.class);
        when(teacherFactory.create(readyRules)).thenReturn(teacher);
        final ResterFactory resterFactory = mock(ResterFactory.class);
        when(resterFactory.create(readyRules)).thenReturn(rester);
        es = new ExpertSystemImpl(readyRules, activeRules, facts, recommendations, thinkerFactory, teacherFactory, resterFactory);
    }

    @Test
    public void mustThink() throws Exception {
        final Set<Recommendation> expected = new HashSet<>();
        final Recommendation recommendation = new Recommendation("@P(A)");
        expected.add(recommendation);

        // given
        when(thinker.think(false, Integer.MAX_VALUE)).thenReturn(expected);

        // when
        final Set<Recommendation> actual = es.think();

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void mustThinkWithoutRuleGeneration() throws Exception {
        final Set<Recommendation> expected = new HashSet<>();
        final Recommendation recommendation = new Recommendation("@P(A)");
        expected.add(recommendation);

        // given
        when(thinker.think(false, Integer.MAX_VALUE)).thenReturn(expected);

        // when
        final Set<Recommendation> actual = es.think(false);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void mustThinkWithRuleGeneration() throws Exception {
        final Set<Recommendation> expected = new HashSet<>();
        final Recommendation recommendation = new Recommendation("@P(A)");
        expected.add(recommendation);

        // given
        when(thinker.think(true, Integer.MAX_VALUE)).thenReturn(expected);

        // when
        final Set<Recommendation> actual = es.think(true);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void mustThinkWithRuleGenerationAndCycles() throws Exception {
        final Set<Recommendation> expected = new HashSet<>();
        final Recommendation recommendation = new Recommendation("@P(A)");
        expected.add(recommendation);

        // given
        when(thinker.think(true, NUM_TEST_CYCLES)).thenReturn(expected);

        // when
        final Set<Recommendation> actual = es.think(true, NUM_TEST_CYCLES);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void testTeach() throws Exception {
        // when
        es.teach(TEST_SENTENCE);
    }

    @Test
    public void testRest() throws Exception {
        // when
       es.rest(NUM_TEST_CYCLES);
    }

    @Test
    public void mustResetRulesFactsAndRecommendations() throws Exception {
        // given
        readyRules.add(new Rule("P(A) -> P(B)"));
        activeRules.add(new Rule("P(C) -> P(D)"));
        final Fact fact = new Fact("P(A)");
        facts.add(fact);
        final Recommendation recommendation = new Recommendation("@P(A)");
        recommendations.add(recommendation);

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
        final Rule readyRule = new Rule("P(A) -> P(B)");
        final Rule activeRule = new Rule("P(C) -> P(D)");
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
        final Set<Tag> tags = new HashSet<>();
        final Rule rule = new Rule("P(A) -> P(B)");
        final Fact fact = new Fact("P(A)");
        final Recommendation recommendation = new Recommendation("@P(A)");
        tags.add(rule);
        tags.add(fact);
        tags.add(recommendation);

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
        final Tag tag = mock(Tag.class);

        // when
        final boolean actualAddResult = es.addTag(tag);

        // then
        assertFalse(actualAddResult);
    }

    @Test
    public void mustAddRuleTag() throws Exception {
        final Rule rule = new Rule("P(A) -> P(B)");

        // when
        es.addTag(rule);

        // then
        assertTrue(readyRules.contains(rule));
        assertTrue(activeRules.isEmpty());
    }

    @Test
    public void mustAddFactTag() throws Exception {
        final Fact fact = new Fact("P(A)");

        // when
        es.addTag(fact);

        // then
        assertTrue(facts.contains(fact));
    }

    @Test
    public void mustAddRecommendationTag() throws Exception {
        final Recommendation recommendation = new Recommendation("@P(A)");

        // when
        es.addTag(recommendation);

        // then
        assertTrue(recommendations.contains(recommendation));
    }

    @Test
    public void mustAddRule() throws Exception {
        final Rule rule = new Rule("P(A) -> P(B)");

        // when
        es.addReadyRule(rule);

        // then
        assertTrue(readyRules.contains(rule));
        assertTrue(activeRules.isEmpty());
    }

    @Test
    public void mustAddFact() throws Exception {
        final Fact fact = new Fact("P(A)");

        // when
        es.addFact(fact);

        // then
        assertTrue(facts.contains(fact));
    }

    @Test
    public void mustAddRecommendation() throws Exception {
        final Recommendation recommendation = new Recommendation("@P(A)");

        // when
        es.addRecommendation(recommendation);

        // then
        assertTrue(recommendations.contains(recommendation));
    }

    @Test
    public void mustRemoveFact() throws Exception {
        final Fact fact = new Fact("P(A)");

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
        final Recommendation recommendation = new Recommendation("@P(A)");
        recommendations.add(recommendation);

        // when
        final Set<Recommendation> actualRecommendations = es.getRecommendations();

        // then
        assertEquals(actualRecommendations, recommendations);
    }

    @Test
    public void mustGetReadyRules() throws Exception {
        // given
        readyRules.add(new Rule("P(A) -> P(B)"));

        // when
        final Set<Rule> actualReadyRules = es.getReadyRules();

        // then
        assertEquals(actualReadyRules, readyRules);
    }

    @Test
    public void mustGetActiveRules() throws Exception {
        // given
        activeRules.add(new Rule("P(A) -> P(B)"));

        // when
        final Set<Rule> actualActiveRules = es.getActiveRules();

        // then
        assertEquals(actualActiveRules, activeRules);
    }

    @Test
    public void mustGetFacts() throws Exception {
        // given
        final Fact fact = new Fact("P(A)");
        facts.add(fact);

        // when
        final Set<Fact> actualFacts = es.getFacts();

        // then
        assertEquals(actualFacts, facts);
    }
}