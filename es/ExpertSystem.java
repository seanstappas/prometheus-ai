package es;

import interfaces.PrometheusLayer;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * Expert System (ES)
 */
public class ExpertSystem implements PrometheusLayer {
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;

    /**
     * Creates an Expert System (ES).
     */
    public ExpertSystem() {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activeRules = new HashSet<>();
    }

    /**
     * Resets the ES by deactivating all Rules, clearing all Facts, and clearing all Recommendations.
     * TODO?: Should reset clear Facts and Recommendations as well?
     */
    public void reset() {
        readyRules.addAll(activeRules);
        activeRules.clear();

        facts.clear();
        recommendations.clear();
    }

    /**
     * Adds a Tag to the ES. Will cast the tag to either a Rule, a Fact, or a Recommendation.
     *
     * @param tag the Tag to be added.
     * @return true if the Tag is successfully added.
     */
    public boolean addTag(Tag tag) {
        switch (tag.type) {
            case RULE:
                addRule((Rule) tag);
                return true;
            case FACT:
                addFact((Fact) tag);
                return true;
            case RECOMMENDATION:
                addRecommendation((Recommendation) tag);
                return true;
        }
        return false;
    }

    /**
     * Adds multiple Tags to the ES.
     *
     * @param tags the Tags to be added.
     * @return true if all the Tags are added successfully.
     */
    public boolean addTags(Set<Tag> tags) {
        boolean allAdded = true;
        for (Tag t : tags) {
            if (!addTag(t)) allAdded = false;
        }
        return allAdded;
    }

    /**
     * Adds a Fact to the ES.
     *
     * @param fact the Fact to be added.
     */
    public void addFact(Fact fact) {
        facts.add(fact);
    }

    /**
     * Adds a Rule to the ES.
     *
     * @param rule the Rule to be added.
     */
    public void addRule(Rule rule) {
        readyRules.add(rule);
    }

    /**
     * Adds a Recommendation to the ES.
     *
     * @param rec the Recommendation to be added.
     */
    public void addRecommendation(Recommendation rec) {
        recommendations.add(rec);
    }

    /**
     * Gets all the Recommendations of the ES.
     *
     * @return the Recommendations of the ES.
     */
    public Set getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the ready Rules of the ES.
     *
     * @return the ready Rules of the ES.
     */
    public Set<Rule> getReadyRules() { // For testing purposes
        return readyRules;
    }

    /**
     * Gets the active Rules of the ES.
     *
     * @return the active Rules of the ES.
     */
    public Set<Rule> getActiveRules() { // For testing purposes
        return activeRules;
    }

    /**
     * Gets the Facts of the ES.
     *
     * @return the Facts of the ES.
     */
    public Set<Fact> getFacts() { // For testing purposes
        return facts;
    }

    /**
     * Continuously iterates through the read Rules, checking Facts and Recommendations, and activating Rules if
     * possible. Stops once the system reaches quiescence.
     * TODO?: Should think() return only the newly activated Recommendations? Or simply all of them? (right now it returns all of them)
     *
     * @return the activated Tags as a result of thinking.
     */
    public Set<Tag> think() {
        Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> activatedTags;
        do {
            activatedTags = thinkCycle();
            allActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        return allActivatedTags;
    }

    /**
     * Makes the ES think for a fixed number of cycles. The number of cycles represents how much effort is being put
     * into thinking. Each cycle is a run-through of all the ready Rules, activating Rules if possible. Note that a Rule
     * that is activated in a cycle is not iterated over in that same cycle, and must wait until the next cycle to
     * cascade further activation.
     *
     * @param numberOfCycles the number of cycles to think for.
     * @return the activated Tags as a result of thinking.
     */
    public Set<Tag> think(int numberOfCycles) {
        Set<Tag> allActivatedTags = new HashSet<>();
        for (int i = 0; i < numberOfCycles; i++) {
            Set<Tag> activatedTags = thinkCycle();
            if (activatedTags.isEmpty())
                break;
            allActivatedTags.addAll(activatedTags);
        }
        return allActivatedTags;
    }


    /**
     * Makes the ES think for a single cycle.
     * TODO: match other tokens in facts: ? < > = (Think how to make this efficient, without iterating through entire set...)
     * TODO?: What was question mark again? Any value?
     *
     * @return the activated Tags as a result of thinking.
     */
    private Set<Tag> thinkCycle() {
        Set<Tag> activatedTags = new HashSet<>();
        Set<Rule> pendingActivatedRules = new HashSet<>();
        for (Rule rule : readyRules) {
            if ((!facts.contains(rule.action) || !recommendations.contains(rule.action))) {
                boolean shouldActivate = true;
                for (Tag condition : rule.conditions) {
                    if (!facts.contains(condition) && !recommendations.contains(condition)) {
                        shouldActivate = false;
                        break;
                    }
                }
                if (shouldActivate) pendingActivatedRules.add(rule);
            }
        }
        for (Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            activeRules.add(rule);
            activatedTags.add(rule.action);
            if (rule.action.isRecommendation()) recommendations.add((Recommendation) rule.action);
            else if (rule.action.isFact()) facts.add((Fact) rule.action);
        }
        return activatedTags;
    }
}
