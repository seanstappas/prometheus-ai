package es;

import java.util.HashSet;
import java.util.Set;

/**
 * Expert System
 */
public class ES {
    private Set<Rule> rules;
    private Set<String> facts; // each predicate as String: '(Px)'
    private Set<String> recommendations;
    // Type of recommendations? Good. Recommendations are for specific actions to be taken (walk, stop...). These recommendations are passed up the chain
    // Recommendations are indicated by some special symbol: (#...)
    // TODO: Should there be a set of already activated rules

    public ES() {
        rules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
//        reset(); // TODO: Why call reset?
    }

    public void reset() {
        rules.clear();
        facts.clear();
        recommendations.clear();
    }

    public void addFact(String fact) { // TODO: Fact is a String? Could have String constructor..
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

    // TODO: Should there be a threshold number of iterations to think()? If so, does iterations represent a cycle like the KNN, or the number of rules activated/facts fired (right now it works with number of facts)

    /**
     * 1. Iterate through rules, checking facts and activating if applicable.
     * 2. Repeat until no activations in a cycle.
     */
    public void think() { // TODO: Complete think() method: set rules from knn.KNN, compare Facts and Rules: populate Rules, Facts
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
        for (Rule rule : rules) {
            if (!rule.activated && (!facts.contains(rule.action) || (isRecommendation(rule.action) && !recommendations.contains(rule.action)))) {
                boolean shouldActivate = true;
                for (String condition : rule.conditions) {
                    if (!facts.contains(condition)) { // TODO: match other tokens in facts: ? < > =
                        shouldActivate = false;
                        break;
                    }
                }
                if (shouldActivate) {
                    rule.activated = true;
                    if (isRecommendation(rule.action))
                        recommendations.add(rule.action);
                    else
                        facts.add(rule.action);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Assuming format of (# )
     * @param action
     * @return
     */
    private boolean isRecommendation(String action) {
        return action.indexOf('c') >= 0;
    }

    private boolean match(String condition, String fact) {
        return condition.equals(fact);
    }
}
