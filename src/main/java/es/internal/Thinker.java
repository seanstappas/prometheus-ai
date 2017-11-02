package es.internal;

import com.google.inject.assistedinject.Assisted;
import tags.Fact;
import tags.Predicate;
import tags.Recommendation;
import tags.Rule;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

class Thinker {
    private final ThinkCycleExecutor thinkCycleExecutor;
    private Set<Rule> readyRules;
    private Set<Fact> facts;

    @Inject
    public Thinker(
            @Assisted("readyRules") Set<Rule> readyRules,
            @Assisted("activeRules") Set<Rule> activeRules,
            @Assisted("facts") Set<Fact> facts,
            @Assisted("recommendations") Set<Recommendation> recommendations,
            ThinkCycleExecutorFactory thinkCycleExecutorFactory) {
        this.readyRules = readyRules;
        this.facts = facts;
        this.thinkCycleExecutor = thinkCycleExecutorFactory.create(readyRules, activeRules, facts, recommendations);
    }

    Set<Recommendation> think(boolean shouldGenerateRule, int numberOfCycles) {
        Set<Predicate> allActivatedPredicates = new HashSet<>();
        Set<Predicate> activatedPredicates;
        Set<Fact> inputFacts = new HashSet<>(facts);
        for (int i = 0; i < numberOfCycles; i++) {
            activatedPredicates = thinkCycleExecutor.thinkCycle();
            if (activatedPredicates.isEmpty())
                break;
            allActivatedPredicates.addAll(activatedPredicates);
        }
        Set<Recommendation> activatedRecommendations = new HashSet<>();
        for (Predicate predicate : allActivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFacts, allActivatedPredicates);
        }
        return activatedRecommendations;
    }

    /**
     * Generates a rule from the facts present in the ES at the beginning of think() and the predicates activated before quiescence
     * <p>
     * Adds generate rule to ES
     *
     * @param inputFactSet           Set of Facts in ES
     * @param allActivatedPredicates Set of activated Predicates
     */
    private void generateProvenRule(Set<Fact> inputFactSet, Set<Predicate> allActivatedPredicates) {
        Set<Predicate> outputPredicates = new HashSet<>();
        outputPredicates.addAll(allActivatedPredicates);
        Rule provenRule = new Rule(inputFactSet, outputPredicates);
        readyRules.add(provenRule);
    }
}
