package es.api;

import java.util.Set;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

/**
 * Expert System (ES).
 */
public interface ExpertSystem {
    /**
     * Makes the ES think.
     *
     * @return the Set of Recommendations activated as a result of thinking.
     * @see #think(boolean)
     */
    Set<Recommendation> think();

    /**
     * Continuously iterates through the read Rules, checking Facts and
     * Recommendations, and activating Rules if possible.
     * <p>
     * Stops once the system reaches natural quiescence and generates a new
     * rule.
     *
     * @param generateRule if true generates the new rule proven by a think
     *                     cycle
     * @return the activated Recommendations as a result of thinking
     */
    Set<Recommendation> think(boolean generateRule);

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
    Set<Recommendation> think(boolean generateRule, int numberOfCycles);

    /**
     * Generates rules from a natural language sentence
     * <p>
     * NB: Sentences must contain one token from {"if", "when", "while",
     * "first"}, and one token from {"then", "next", "do"}, to denote input
     * facts and output predicates respectively.
     * <p>
     * e.g. "If Human(near) Then Move(steps=10)"
     *
     * @param sentence that contains input and output delimiters
     */
    void teach(String sentence);

    /**
     * Process that occurs when ES is not thinking.
     * <p>
     * Currently calls addReadyRule to merge rules
     *
     * @param numberOfCycles the number of rest cycles
     */
    void rest(int numberOfCycles);

    /**
     * Resets the ES by clearing all Rules, Recommendations, and Facts.
     */
    void reset();

    /**
     * Deactivates all active Rules.
     */
    void deactivateRules();

    /**
     * Adds multiple Tags to the ES.
     *
     * @param tags the Tags to be added
     */
    void addTags(Set<Tag> tags);

    /**
     * Adds a Tag to the ES. Will cast the tag to either a Rule, a Fact, or a
     * Recommendation.
     *
     * @param tag the Tag to be added
     * @return <code>true</code> if the Tag is successfully added
     */
    boolean addTag(Tag tag);

    /**
     * Adds a Fact to the ES.
     *
     * @param fact the Fact to be added
     * @return <code>true</code> if the ES did not already contain the specified
     * Fact
     */
    boolean addFact(Fact fact);

    /**
     * Removes a fact from the ES.
     *
     * @param fact the Fact to be removed
     * @return <code>true</code> if the ES contained the specified Fact
     */
    boolean removeFact(Fact fact);

    /**
     * Adds a Rule to the ES.
     *
     * @param rule the Rule to be added
     * @return <code>true</code> if the ES did not already contain the specified
     * Rule
     */
    boolean addReadyRule(Rule rule);

    /**
     * Removes a Rule from the ES.
     *
     * @param rule the Rule to be remove
     * @return <code>true</code> if the ES contained the specified Rule
     */
    boolean removeReadyRule(Rule rule);

    /**
     * Adds a Recommendation to the ES.
     *
     * @param rec the Recommendation to be added
     * @return <code>true</code> if the ES did not already contain the specified
     * Recommendation
     */
    boolean addRecommendation(Recommendation rec);

    /**
     * Gets all the Recommendations of the ES.
     *
     * @return the Recommendations of the ES
     */
    Set<Recommendation> getRecommendations();

    /**
     * Gets the ready Rules of the ES.
     *
     * @return the ready Rules of the ES
     */
    Set<Rule> getReadyRules();

    /**
     * Gets the active Rules of the ES.
     *
     * @return the active Rules of the ES
     */
    Set<Rule> getActiveRules();

    /**
     * Gets the Facts of the ES.
     *
     * @return the Facts of the ES
     */
    Set<Fact> getFacts();
}
