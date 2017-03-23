package es;

import tags.Rule;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * Expert System
 */
public class ExpertSystem {
    // TODO: Should think() return the newly activated recommendations? Or simply all of them?
    private Set<Rule> readyRules;
    private Set<Rule> activatedRules;
    private Set<Tag> facts; // each predicate as String: '(Px)'
    private Set<Tag> recommendations;
    // Type of recommendations? Good. Recommendations are for specific actions to be taken (walk, stop...). These recommendations are passed up the chain
    // Recommendations are indicated by some special symbol: (#...)
    // Should there be a set of already activated readyRules (or just boolean like now?) Yes... reset will bring them back

    public ExpertSystem() {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activatedRules = new HashSet<>();
    }

    public void reset() { // TODO: Should reset clear all data structures, or only de-activate activated rules?
        readyRules.clear();
        facts.clear();
        recommendations.clear();
    }

    public void addTag(Tag t) {
        switch (t.type) {
            case RULE:
                addRule((Rule) t);
                break;
            case FACT:
                addFact(t);
                break;
            case RECOMMENDATION:
                addRecommendation(t);
                break;
        }
    }

    public void addTags(Set<Tag> tags) {
        for (Tag t : tags) {
            addTag(t);
        }
    }

    public void addFact(Tag fact) {
        facts.add(fact);
    }

    public void addRule(Rule rule) {
        readyRules.add(rule);
    }

    public void addRecommendation(Tag rec) {
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

    public Set<Tag> getFacts() { // For testing purposes
        return facts;
    }

    // Should there be a threshold number of iterations to think()? If so, does iterations represent a cycle like the KnowledgeNodeNetwork, or the number of readyRules activated/facts fired (right now it works with number of facts) The cycle should be like the KnowledgeNodeNetwork (one run-through of readyRules)

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
            if ((!facts.contains(rule.action) || !recommendations.contains(rule.action))) { // With Tag superclass, don't need to distinguish between Recommendation and Fact here
                boolean shouldActivate = true;
                for (Tag condition : rule.conditions) {
                    if (!facts.contains(condition) && !recommendations.contains(condition)) { // TODO: match other tokens in facts: ? < > = (What was question mark again? Any value?) Think how to make this efficient, without iterating through entire set...
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
            if (rule.action.isRecommendation())
                recommendations.add(rule.action);
            else
                facts.add(rule.action);
        }
        return fired;
    }
}
