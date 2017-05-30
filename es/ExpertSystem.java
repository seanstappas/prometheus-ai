package es;

import interfaces.PrometheusLayer;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Expert System (ES)
 */
public class ExpertSystem implements PrometheusLayer {
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private HashMap<String, Set<Fact>> facts;
    private Set<Recommendation> recommendations;

    /**
     * Creates an Expert System (ES).
     */
    public ExpertSystem() {
        readyRules = new HashSet<>();
        facts = new HashMap<>();
        recommendations = new HashSet<>();
        activeRules = new HashSet<>();
    }

    /**
     * Resets the ES by clearing all Rules, Recommendations, and Facts.
     */
    public void reset() {
        activeRules.clear();
        readyRules.clear();
        facts.clear();
        recommendations.clear();
    }

    /**
     * Deactivates all active Rules.
     */
    public void deactivateRules() {
        readyRules.addAll(activeRules);
        activeRules.clear();
    }

    /**
     * Adds a Tag to the ES. Will cast the tag to either a Rule, a Fact, or a Recommendation.
     *
     * @param tag  the Tag to be added
     * @return     <code>true</code> if the Tag is successfully added
     */
    public boolean addTag(Tag tag) {
        switch (tag.type) {
            case RULE:
                return addRule((Rule) tag);
            case FACT:
                return addFact((Fact) tag);
            case RECOMMENDATION:
                return addRecommendation((Recommendation) tag);
        }
        return false;
    }

    /**
     * Adds multiple Tags to the ES.
     *
     * @param tags  the Tags to be added
     * @return      <code>true</code> if all the Tags are added successfully
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
     * @param fact  the Fact to be added
     * @return      <code>true</code> if the ES did not already contain the specified Fact
     */
    public boolean addFact(Fact fact) {
        String key = fact.toVariable();
        if (facts.get(key)!= null) {
            return facts.get(key).add(fact);
        }
        else {
            Set newSet = new HashSet();
            newSet.add(fact);
            facts.put(key, newSet);
            return true;
        }
    }

    /**
     * Adds a Rule to the ES.
     *
     * @param rule  the Rule to be added
     * @return      <code>true</code> if the ES did not already contain the specified Rule
     */
    public boolean addRule(Rule rule) {
        return readyRules.add(rule);
    }

    /**
     * Adds a Recommendation to the ES.
     *
     * @param rec  the Recommendation to be added
     * @return      <code>true</code> if the ES did not already contain the specified Recommendation
     */
    public boolean addRecommendation(Recommendation rec) {
        return recommendations.add(rec);
    }

    /**
     * Gets all the Recommendations of the ES.
     *
     * @return  the Recommendations of the ES
     */
    public Set<Recommendation> getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the ready Rules of the ES.
     *
     * @return  the ready Rules of the ES
     */
    public Set<Rule> getReadyRules() { // For testing purposes
        return readyRules;
    }

    /**
     * Gets the active Rules of the ES.
     *
     * @return  the active Rules of the ES
     */
    public Set<Rule> getActiveRules() { // For testing purposes
        return activeRules;
    }

    /**
     * Gets the Facts of the ES.
     *
     * @return  the Facts of the ES
     */
    public Set<Fact> getFacts() {
        Set<Fact> factsSet = new HashSet<>();
        for (String k: facts.keySet()) {
            for (Fact v : facts.get(k)) {
                factsSet.add(v);
            }
        }
        return factsSet;
    }

    /**
     * Continuously iterates through the read Rules, checking Facts and Recommendations, and activating Rules if
     * possible. Stops once the system reaches natural quiescence.
     *
     * @return  the activated Recommendations as a result of thinking
     */
    public Set<Tag> think() {
        Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> activatedTags;
        do {
            activatedTags = thinkCycle();
            allActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Tag tag : allActivatedTags) {
            if (tag.isRecommendation())
                activatedRecommendations.add(tag);
        }
        return activatedRecommendations;
    }

    /**
     * Makes the ES think for a fixed number of cycles. The number of cycles represents how much effort is being put
     * into thinking. Each cycle is a run-through of all the ready Rules, activating Rules if possible. Note that a Rule
     * that is activated in a cycle is not iterated over in that same cycle, and must wait until the next cycle to
     * cascade further activation. This is threshold quiescence, which may or may not correspond with natural
     * quiescence.
     *
     * @param numberOfCycles  the number of cycles to think for
     * @return                the activated Recommendations as a result of thinking
     */
    public Set<Tag> think(int numberOfCycles) {
        Set<Tag> allActivatedTags = new HashSet<>();
        for (int i = 0; i < numberOfCycles; i++) {
            Set<Tag> activatedTags = thinkCycle();
            if (activatedTags.isEmpty())
                break;
            allActivatedTags.addAll(activatedTags);
        }
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Tag tag : allActivatedTags) {
            if (tag.isRecommendation())
                activatedRecommendations.add(tag);
        }
        return activatedRecommendations;
    }


    /**
     * Makes the ES think for a single cycle.
     *
     * @return  the activated Tags as a result of thinking
     */
    private Set<Tag> thinkCycle() {
        Set<Tag> activatedTags = new HashSet<>();
        Set<Rule> pendingActivatedRules = new HashSet<>();
        for (Rule rule : readyRules) {
            boolean shouldActivate = true;
            for (Fact fact : rule.inputFacts) {
                if (!facts.containsKey(fact.toVariable())) {
                    shouldActivate = false;
                    break;
                }
                else {
                    Set factSet = facts.get(fact.toVariable());
                    if (fact.matches(factSet)) {
                        shouldActivate = true;
                        break;
                    }
                }
            }
            if (shouldActivate)
                pendingActivatedRules.add(rule);
        }
        for (Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            activeRules.add(rule);
            for (Tag tag : rule.outputTags) {
                if (!facts.keySet().contains(tag.toString()) && !recommendations.contains(tag.toString())) {
                    activatedTags.add(tag);
                    addTag(tag);
                }
            }
        }
        return activatedTags;
    }
}
