package es;

import tags.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Expert System
 */
public class ES {
    // TODO: Refactor everything with Tag objects
    private Set<Rule> rules;
    private Set<Rule> activatedRules;
    private Set<String> facts; // each predicate as String: '(Px)'
    private Set<String> recommendations;
    // Type of recommendations? Good. Recommendations are for specific actions to be taken (walk, stop...). These recommendations are passed up the chain
    // Recommendations are indicated by some special symbol: (#...)
    // Should there be a set of already activated rules (or just boolean like now?) Yes... reset will bring them back

    public ES() {
        rules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
//        reset(); // Why call reset? Bring back used rules
    }

    public void reset() {
        rules.clear();
        facts.clear();
        recommendations.clear();
    }

    public void addFact(String fact) {
        facts.add(fact);
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    private void addRecommendation(String rec) {
        recommendations.add(rec);
    }

    public Set getRecommendations() {
        return recommendations;
    }

    public Set<Rule> getRules() { // For testing purposes
        return rules;
    }

    public Set<String> getFacts() { // For testing purposes
        return facts;
    }

    // Should there be a threshold number of iterations to think()? If so, does iterations represent a cycle like the KNN, or the number of rules activated/facts fired (right now it works with number of facts) The cycle should be like the KNN (one run-through of rules)

    /**
     * 1. Iterate through rules, checking facts and activating if applicable.
     * 2. Repeat until no activations in a cycle.
     */
    public void think() {
        boolean fired;
        do {
            fired = thinkCycle();
        } while (fired); // Need to re-check rules every time
    }

    public void think(int numberOfCycles) {
        for (int i = 0; i < numberOfCycles; i++) {
            thinkCycle();
        }
    }

    private boolean thinkCycle() { // Returns true if a rule activated
        boolean fired = false;
        for (Rule rule : rules) {
            if (!rule.activated && (!facts.contains(rule.action) || (isRecommendation(rule.action) && !recommendations.contains(rule.action)))) { // With Tag superclass, don't need to distinguish between Recommendation and Fact here
                boolean shouldActivate = true;
                for (String condition : rule.conditions) {
                    if (!facts.contains(condition)) { // TODO: match other tokens in facts: ? < > =
                        shouldActivate = false;
                        break;
                    }
                }
                if (shouldActivate) {
                    fired = true;
                    rule.activated = true;
                    if (isRecommendation(rule.action)) // Can do instanceof check here instead...
                        recommendations.add(rule.action);
                    else
                        facts.add(rule.action);
                }
            }
        }
        return fired;
    }

    /**
     * Assuming format of (# )
     * @param tag The tag to check.
     * @return Whether or not the tag is a recommendation.
     */
    private boolean isRecommendation(String tag) {
        return tag.indexOf('c') >= 0;
    }

    private boolean match(String condition, String fact) {
        return condition.equals(fact);
    }
}
