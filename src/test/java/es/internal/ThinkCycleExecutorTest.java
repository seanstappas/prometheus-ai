package es.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Predicate;
import tags.Recommendation;
import tags.Rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ThinkCycleExecutorTest {
    private ThinkCycleExecutor thinkCycleExecutor;
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;


    @BeforeMethod
    public void setUp() throws Exception {
        readyRules = new HashSet<>();
        activeRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        thinkCycleExecutor = new ThinkCycleExecutor(readyRules, activeRules, facts, recommendations);
    }

    @Test
    public void mustThinkCycle() throws Exception {
        // given
        Fact fact = new Fact("P(A)");
        Recommendation recommendation = new Recommendation("@P(B)");
        Rule rule = new Rule(
                Collections.singleton(fact),
                Collections.singleton(recommendation));
        Set<Predicate> expectedActivatedPredicates = new HashSet<>(
                Collections.singletonList(recommendation)
        );

        readyRules.add(rule);
        facts.add(fact);

        // when
        Set<Predicate> actualActivatedPredicates = thinkCycleExecutor.thinkCycle();

        // then
        assertEquals(expectedActivatedPredicates, actualActivatedPredicates);
        assertTrue(readyRules.isEmpty());
        assertTrue(activeRules.contains(rule));
        assertTrue(facts.contains(fact));
        assertTrue(recommendations.contains(recommendation));
    }

}