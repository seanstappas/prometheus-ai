package es.internal;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import com.google.inject.assistedinject.Assisted;
import tags.Fact;
import tags.Predicate;
import tags.Recommendation;
import tags.Rule;

/**
 * Thinker which executes thinking cycles.
 */
class Thinker {
    private final ThinkCycleExecutor thinkCycleExecutor;
    private final Set<Rule> readyRules;
    private final Set<Fact> facts;

    @Inject
    Thinker(
            @Assisted("readyRules") final Set<Rule> readyRules,
            @Assisted("activeRules") final Set<Rule> activeRules,
            @Assisted("facts") final Set<Fact> facts,
            @Assisted("recommendations")
            final Set<Recommendation> recommendations,
            final ThinkCycleExecutorFactory thinkCycleExecutorFactory) {
        this.readyRules = readyRules;
        this.facts = facts;
        this.thinkCycleExecutor = thinkCycleExecutorFactory
                .create(readyRules, activeRules, facts, recommendations);
    }

    /**
     * Makes the ES think for a fixed number of cycles. The number of cycles
     * represents how much effort is being put into thinking. Each cycle is a
     * run-through of all the ready Rules, activating Rules if possible. Note
     * that a Rule that is activated in a cycle is not iterated over in that
     * same cycle, and must wait until the next cycle to cascade further
     * activation. This is threshold quiescence, which may or may not correspond
     * with natural quiescence. Generates a new rule.
     *
     * @param generateRule   if true generates the new rule proven by a think
     *                       cycle
     * @param numberOfCycles the number of cycles to think for
     * @return the activated Recommendations as a result of thinking
     */
    Set<Recommendation> think(final boolean generateRule,
                              final int numberOfCycles) {
        final Set<Predicate> allActivatedPredicates = new HashSet<>();
        Set<Predicate> activatedPredicates;
        final Set<Fact> inputFacts = new HashSet<>(facts);
        for (int i = 0; i < numberOfCycles; i++) {
            activatedPredicates = thinkCycleExecutor.thinkCycle();
            if (activatedPredicates.isEmpty()) {
                break;
            }
            allActivatedPredicates.addAll(activatedPredicates);
        }
        final Set<Recommendation> activatedRecommendations = new HashSet<>();
        for (final Predicate predicate : allActivatedPredicates) {
            if (predicate instanceof Recommendation) {
                activatedRecommendations.add((Recommendation) predicate);
            }
        }
        if (generateRule) {
            generateProvenRule(inputFacts, allActivatedPredicates);
        }
        return activatedRecommendations;
    }

    /**
     * Generates a rule from the facts present in the ES at the beginning of
     * think() and the predicates activated before quiescence.
     * <p>
     * Adds generate rule to ES
     *
     * @param inputFactSet           Set of Facts in ES
     * @param allActivatedPredicates Set of activated Predicates
     */
    private void generateProvenRule(
            final Set<Fact> inputFactSet,
            final Set<Predicate> allActivatedPredicates) {
        final Set<Predicate> outputPredicates = new HashSet<>();
        outputPredicates.addAll(allActivatedPredicates);
        final Rule provenRule = new Rule(inputFactSet, outputPredicates);
        readyRules.add(provenRule);
    }
}
