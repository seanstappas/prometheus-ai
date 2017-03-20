package es;

import tags.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Expert System
 */
public class ES {
    // TODO: Refactor everything with Tag objects
    private Set<Rule> readyRules;
    private Set<Rule> activatedRules;
    private Set<String> facts; // each predicate as String: '(Px)'
    private Set<String> recommendations;
    // Type of recommendations? Good. Recommendations are for specific actions to be taken (walk, stop...). These recommendations are passed up the chain
    // Recommendations are indicated by some special symbol: (#...)
    // Should there be a set of already activated readyRules (or just boolean like now?) Yes... reset will bring them back

    public ES() {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activatedRules = new HashSet<>();
    }

    public void reset() {
        readyRules.clear();
        facts.clear();
        recommendations.clear();
    }

    public void addFact(String fact) {
        facts.add(fact);
    }

    public void addRule(Rule rule) {
        readyRules.add(rule);
    }

    private void addRecommendation(String rec) {
        recommendations.add(rec);
    }

    public Set getRecommendations() {
        return recommendations;
    }

    public Set<Rule> getReadyRules() { // For testing purposes
        return readyRules;
    }

    public Set<Rule> getActivatedRules() { // For testing purposes
        return activatedRules;
    }

    public Set<String> getFacts() { // For testing purposes
        return facts;
    }

    // Should there be a threshold number of iterations to think()? If so, does iterations represent a cycle like the KNN, or the number of readyRules activated/facts fired (right now it works with number of facts) The cycle should be like the KNN (one run-through of readyRules)

    /**
     * 1. Iterate through readyRules, checking facts and activating if applicable.
     * 2. Repeat until no activations in a cycle.
     */
    public void think() {
        boolean fired;
        do {
            fired = thinkCycle();
        } while (fired); // Need to re-check readyRules every time
    }

    public void think(int numberOfCycles) {
        for (int i = 0; i < numberOfCycles; i++) {
            if (!thinkCycle())
                return;
        }
    }

    private boolean thinkCycle() { // Returns true if a rule activated
        boolean fired = false;
        Set<Rule> pendingActivatedRules = new HashSet<>();
        for (Rule rule : readyRules) {
            if ((!facts.contains(rule.action.value) || (rule.action.isReccomendation && !recommendations.contains(rule.action.value)))) { // With Tag superclass, don't need to distinguish between Recommendation and Fact here
                boolean shouldActivate = true;
                for (String condition : rule.conditions) {
                    if (!facts.contains(condition)) { // TODO: match other tokens in facts: ? < > =
                        shouldActivate = false;
                        break;
                    }
                }
                if (shouldActivate) {
                    pendingActivatedRules.add(rule);
                    fired = true;
                }
            }
        }
        for (Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            activatedRules.add(rule);
            if (rule.action.isReccomendation)
                recommendations.add(rule.action.value);
            else
                facts.add(rule.action.value);
        }
        return fired;
    }
}
