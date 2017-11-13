package es.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Predicate;
import tags.Rule;
import tags.Tag;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;


public class RuleMergerTest {
    private RuleMerger ruleMerger;

    @BeforeMethod
    public void setUp() throws Exception {
        ruleMerger = new RuleMerger();
    }

    @Test
    public void mustMakeEmptyRule() throws Exception {
        Set<Rule> rules = new HashSet<>();
        Rule rule1 = mock(Rule.class);
        Rule rule2 = mock(Rule.class);
        rules.add(rule1);
        rules.add(rule2);

        Set<Fact> facts1 = new HashSet<>();
        Fact fact1 = mock(Fact.class);
        facts1.add(fact1);


        Set<Fact> facts2 = new HashSet<>();
        Fact fact2 = mock(Fact.class);
        facts2.add(fact2);

        Set<Predicate> predicates1 = new HashSet<>();
        Fact p1 = mock(Fact.class);
        predicates1.add(p1);


        Set<Predicate> predicates2 = new HashSet<>();
        Fact p2 = mock(Fact.class);
        predicates2.add(p2);

        // given
        when(fact1.getType()).thenReturn(Tag.TagType.FACT);
        when(fact2.getType()).thenReturn(Tag.TagType.FACT);
        when(p1.getType()).thenReturn(Tag.TagType.FACT);
        when(p2.getType()).thenReturn(Tag.TagType.FACT);

        when(fact1.matches(p2)).thenReturn(false);
        when(fact2.matches(p1)).thenReturn(false);

        when(rule1.getInputFacts()).thenReturn(facts1);
        when(rule1.getOutputPredicates()).thenReturn(predicates1);
        when(rule2.getInputFacts()).thenReturn(facts2);
        when(rule2.getOutputPredicates()).thenReturn(predicates2);


        // when
        Optional<Rule> actual = ruleMerger.makeMergedRule(rules);

        // then
        assertEquals(actual, Optional.empty());
    }

    @Test
    public void mustMakeMergedRule() throws Exception {
        Set<Rule> rules = new HashSet<>();
        Rule rule1 = mock(Rule.class);
        Rule rule2 = mock(Rule.class);
        rules.add(rule1);
        rules.add(rule2);

        Set<Fact> facts1 = new HashSet<>();
        Fact fact1 = mock(Fact.class);
        facts1.add(fact1);


        Set<Fact> facts2 = new HashSet<>();
        Fact fact2 = mock(Fact.class);
        facts2.add(fact2);

        Set<Predicate> predicates1 = new HashSet<>();
        Fact p1 = mock(Fact.class);
        predicates1.add(p1);


        Set<Predicate> predicates2 = new HashSet<>();
        Fact p2 = mock(Fact.class);
        predicates2.add(p2);

        // given
        when(fact1.getType()).thenReturn(Tag.TagType.FACT);
        when(fact2.getType()).thenReturn(Tag.TagType.FACT);
        when(p1.getType()).thenReturn(Tag.TagType.FACT);
        when(p2.getType()).thenReturn(Tag.TagType.FACT);

        when(p2.matches(fact1)).thenReturn(false);
        when(fact1.matches(p2)).thenReturn(false);

        when(p1.matches(fact2)).thenReturn(true);
        when(fact2.matches(p1)).thenReturn(true);

        when(rule1.getInputFacts()).thenReturn(facts1);
        when(rule1.getOutputPredicates()).thenReturn(predicates1);
        when(rule2.getInputFacts()).thenReturn(facts2);
        when(rule2.getOutputPredicates()).thenReturn(predicates2);

        // when
        Optional<Rule> actual = ruleMerger.makeMergedRule(rules);

        // then
        assertNotEquals(actual, Optional.empty());
        assertEquals(actual.get().getInputFacts(), facts1);
        assertEquals(actual.get().getOutputPredicates(), predicates2);
    }

}