package es.internal;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Predicate;
import tags.Rule;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;


public class RuleMergerTest {
    private RuleMerger ruleMerger;

    @BeforeMethod
    public void setUp() throws Exception {
        ruleMerger = new RuleMerger();
    }

    @Test
    public void mustMakeEmptyRule() throws Exception {
        final Set<Rule> rules = new HashSet<>();
        final Set<Fact> facts1 = new HashSet<>();
        final Set<Fact> facts2 = new HashSet<>();
        final Set<Predicate> predicates1 = new HashSet<>();
        final Set<Predicate> predicates2 = new HashSet<>();

        // given
        final Fact fact1 = new Fact("P(A)");
        facts1.add(fact1);
        final Fact p1 = new Fact("P(B)");
        predicates1.add(p1);
        final Rule rule1 = new Rule(facts1, predicates1);
        rules.add(rule1);

        final Fact fact2 = new Fact("P(C)");
        facts2.add(fact2);
        final Fact p2 = new Fact("P(D)");
        predicates2.add(p2);
        final Rule rule2 = new Rule(facts2, predicates2);
        rules.add(rule2);


        // when
        final Optional<Rule> actual = ruleMerger.makeMergedRule(rules);

        // then
        assertEquals(actual, Optional.empty());
    }

    @Test
    public void mustMakeMergedRule() throws Exception {
        final Set<Rule> rules = new HashSet<>();
        final Set<Fact> facts1 = new HashSet<>();
        final Set<Fact> facts2 = new HashSet<>();
        final Set<Predicate> predicates1 = new HashSet<>();
        final Set<Predicate> predicates2 = new HashSet<>();

        // given
        final Fact fact1 = new Fact("P(A)");
        facts1.add(fact1);
        final Fact p1 = new Fact("P(B)");
        predicates1.add(p1);
        final Rule rule1 = new Rule(facts1, predicates1);
        rules.add(rule1);

        final Fact fact2 = new Fact("P(B)");
        facts2.add(fact2);
        final Fact p2 = new Fact("P(C)");
        predicates2.add(p2);
        final Rule rule2 = new Rule(facts2, predicates2);
        rules.add(rule2);

        // when
        final Optional<Rule> actual = ruleMerger.makeMergedRule(rules);

        // then
        assertNotEquals(actual, Optional.empty());
        assertEquals(actual.get().getInputFacts(), facts1);
        assertEquals(actual.get().getOutputPredicates(), predicates2);
    }

}
