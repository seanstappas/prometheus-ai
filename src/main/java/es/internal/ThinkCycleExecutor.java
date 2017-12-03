package es.internal;

import com.google.inject.assistedinject.Assisted;
import tags.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ThinkCycleExecutor {
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;

    @Inject
    public ThinkCycleExecutor(
            @Assisted("readyRules") Set<Rule> readyRules,
            @Assisted("activeRules") Set<Rule> activeRules,
            @Assisted("facts") Set<Fact> facts,
            @Assisted("recommendations") Set<Recommendation> recommendations) {
        this.readyRules = readyRules;
        this.activeRules = activeRules;
        this.facts = facts;
        this.recommendations = recommendations;
    }

    /**
     * Makes the ES think for a single cycle.
     * <p>
     * Output predicates of activated rules are replaced if they contain variable arguments e.g. &x
     *
     * @return the activated Predicates as a result of thinking
     */
    Set<Predicate> thinkCycle() {
        Set<Rule> pendingActivatedRules = new HashSet<>();
        Map<String, Argument> pendingReplacementPairs = new HashMap<>();
        for (Rule rule : readyRules) {
            boolean shouldActivate = true;
            for (Fact fact : rule.getInputFacts()) {
                if (!factsContains(fact, pendingReplacementPairs)) {
                    shouldActivate = false;
                    break;
                }
            }
            if (shouldActivate) {
                pendingActivatedRules.add(rule);
            }
        }
        return activateRulesAndReplaceVariableArguments(pendingActivatedRules, pendingReplacementPairs);
    }

    /**
     * Adds a Predicate to the ES. Will cast the tag to either a Rule, a Fact.
     *
     * @param predicate the Predicate to be added
     * @return <code>true</code> if the Predicate is successfully added
     */
    private boolean addPredicate(Predicate predicate) {
        if (predicate instanceof Fact)
            return facts.add((Fact) predicate);
        else if (predicate instanceof Recommendation)
            return recommendations.add((Recommendation) predicate);
        return false;
    }

    /**
     * Checks if a particular fact getMatchResult with any other fact in the ES
     * If inputFact contains a variable argument, matching pair placed in pendingReplacementPairs
     *
     * @param inputFact fact contained in a Rule
     * @return true if (at least) two facts match
     */
    private boolean factsContains(Fact inputFact, Map<String, Argument> pendingReplacementPairs) {
        boolean result = false;
        for (Fact f : facts) {
            VariableReturn matchesResult = f.getMatchResult(inputFact);
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
     * Replaces variable argument(s) within a Predicate with a String or Numeric Argument
     *
     * @param predicate the Predicate that has arguments to replace
     */
    private void replaceVariableArguments(Predicate predicate, Map<String, Argument> pendingReplacementPairs) {
        int argumentIndex = 0;

        for (Argument argument : predicate.getArguments()) {
            if (pendingReplacementPairs.containsKey(argument.getName())) {
                predicate.getArguments().set(argumentIndex, pendingReplacementPairs.get(argument.getName()));
            }
            argumentIndex++;
        }
    }

    /**
     * Activates the Rules specified by the given pending activated Rules and pending replacement pairs
     *
     * @param pendingActivatedRules the pending Rules to activate
     * @param pendingReplacementPairs the pending argument pairs to replace variable arguments with
     * @return the set of Predicates activated
     */
    private Set<Predicate> activateRulesAndReplaceVariableArguments(
            Set<Rule> pendingActivatedRules,
            Map<String, Argument> pendingReplacementPairs) {
        Set<Predicate> activatedPredicates = new HashSet<>();
        for (Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            activeRules.add(rule);
            for (Predicate predicate : rule.getOutputPredicates()) {
                replaceVariableArguments(predicate, pendingReplacementPairs);
                activatedPredicates.add(predicate);
                addPredicate(predicate);
            }
        }
        return activatedPredicates;
    }
}
