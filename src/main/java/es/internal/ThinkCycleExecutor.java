package es.internal;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.inject.assistedinject.Assisted;
import tags.Argument;
import tags.Fact;
import tags.Predicate;
import tags.Recommendation;
import tags.Rule;
import tags.VariableReturn;

/**
 * Executes a single think cycle in the ES.
 */
class ThinkCycleExecutor {
    private final Set<Rule> readyRules;
    private final Set<Rule> activeRules;
    private final Set<Fact> facts;
    private final Set<Recommendation> recommendations;

    @Inject
    ThinkCycleExecutor(
            @Assisted("readyRules") final Set<Rule> readyRules,
            @Assisted("activeRules") final Set<Rule> activeRules,
            @Assisted("facts") final Set<Fact> facts,
            @Assisted("recommendations")
            final Set<Recommendation> recommendations) {
        this.readyRules = readyRules;
        this.activeRules = activeRules;
        this.facts = facts;
        this.recommendations = recommendations;
    }

    /**
     * Makes the ES think for a single cycle.
     * <p>
     * Output predicates of activated rules are replaced if they contain
     * variable arguments e.g. &x
     *
     * @return the activated Predicates as a result of thinking
     */
    Set<Predicate> thinkCycle() {
        final Set<Rule> pendingActivatedRules = new HashSet<>();
        final Map<String, Argument> pendingReplacementPairs = new HashMap<>();
        for (final Rule rule : readyRules) {
            boolean shouldActivate = true;
            for (final Fact fact : rule.getInputFacts()) {
                if (!factsContains(fact, pendingReplacementPairs)) {
                    shouldActivate = false;
                    break;
                }
            }
            if (shouldActivate) {
                pendingActivatedRules.add(rule);
            }
        }
        return activateRulesAndReplaceVariableArguments(pendingActivatedRules,
                pendingReplacementPairs);
    }

    /**
     * Checks if a particular fact getMatchResult with any other fact in the ES.
     * If inputFact contains a variable argument, matching pair placed in
     * pendingReplacementPairs
     *
     * @param inputFact               fact contained in a Rule
     * @param pendingReplacementPairs the pending replacement pairs
     * @return true if (at least) two facts match
     */
    private boolean factsContains(
            final Fact inputFact,
            final Map<String, Argument> pendingReplacementPairs) {
        boolean result = false;
        for (final Fact f : facts) {
            final VariableReturn matchesResult = f.getMatchResult(inputFact);
            if (matchesResult.isFactMatch()) {
                if (matchesResult.getPairs().size() > 0) {
                    pendingReplacementPairs.putAll(matchesResult.getPairs());
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * Activates the Rules specified by the given pending activated Rules and
     * pending replacement pairs.
     *
     * @param pendingActivatedRules   the pending Rules to activate
     * @param pendingReplacementPairs the pending argument pairs to replace
     *                                variable arguments with
     * @return the set of Predicates activated
     */
    private Set<Predicate> activateRulesAndReplaceVariableArguments(
            final Set<Rule> pendingActivatedRules,
            final Map<String, Argument> pendingReplacementPairs) {
        final Set<Predicate> activatedPredicates = new HashSet<>();
        for (final Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            final Set<Predicate> modifiedOutputPredicates = new HashSet<>();
            for (final Predicate predicate : rule.getOutputPredicates()) {
                final Predicate replacedVarArgsPredicate = predicate
                        .replaceVariableArguments(pendingReplacementPairs);
                modifiedOutputPredicates.add(replacedVarArgsPredicate);
                activatedPredicates.add(replacedVarArgsPredicate);
                addPredicate(replacedVarArgsPredicate);
            }
            final Rule modifiedRule =
                    new Rule(rule.getInputFacts(), modifiedOutputPredicates,
                            rule.getConfidence());
            activeRules.add(modifiedRule);
        }
        return activatedPredicates;
    }

    /**
     * Adds a Predicate to the ES. Will cast the tag to either a Rule, a Fact.
     *
     * @param predicate the Predicate to be added
     * @return <code>true</code> if the Predicate is successfully added
     */
    private boolean addPredicate(final Predicate predicate) {
        if (predicate instanceof Fact) {
            return facts.add((Fact) predicate);
        } else if (predicate instanceof Recommendation) {
            return recommendations.add((Recommendation) predicate);
        }
        return false;
    }
}
