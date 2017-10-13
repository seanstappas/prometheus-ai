package es.api;

import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.Set;

/**
 * Created by Sean on 10/13/2017.
 */
public interface ExpertSystem {
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
     * @deprecated
     */
    void addTags(Set<Tag> tags);

    /**
     * Adds a Fact to the ES.
     *
     * @param fact the Fact to be added
     * @return <code>true</code> if the ES did not already contain the specified Fact
     */
    boolean addFact(Fact fact);

    /**
     * Removes a fact from the ES.
     * @param fact the Fact to be removed
     * @return <code>true</code> if the ES did not remove the specified Fact
     */
    boolean removeFact(Fact fact);

    /**
     * Adds a Rule to the ES.
     *
     * @param rule the Rule to be added
     * @return <code>true</code> if the ES did not already contain the specified Rule
     */
    boolean addRule(Rule rule);

    /**
     * Adds a Recommendation to the ES.
     *
     * @param rec the Recommendation to be added
     * @return <code>true</code> if the ES did not already contain the specified Recommendation
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

    /**
     * {@code shouldGenerateRule} defaults to false
     * @see #think(boolean)
     */
    Set<Tag> think();

    /**
     * Continuously iterates through the read Rules, checking Facts and Recommendations, and activating Rules if
     * possible.
     * <p>
     * Stops once the system reaches natural quiescence and generates a new rule.
     *
     * @param shouldGenerateRule if true generates the new rule proven by a think cycle
     * @return the activated Recommendations as a result of thinking
     */
    Set<Tag> think(boolean shouldGenerateRule);

    /**
     * Makes the ES think for a fixed number of cycles. The number of cycles represents how much effort is being put
     * into thinking. Each cycle is a run-through of all the ready Rules, activating Rules if possible. Note that a Rule
     * that is activated in a cycle is not iterated over in that same cycle, and must wait until the next cycle to
     * cascade further activation. This is threshold quiescence, which may or may not correspond with natural
     * quiescence. Generates a new rule.
     *
     * @param numberOfCycles the number of cycles to think for
     * @param shouldGenerateRule if true generates the new rule proven by a think cycle
     * @return the activated Recommendations as a result of thinking
     */
    Set<Tag> think(int numberOfCycles, boolean shouldGenerateRule);

    /**
     * Process that occurs when ES is not thinking.
     * <p>
     * Currently calls addRule to merge rules
     *
     * @param numberOfCycles how many cycles over the ruleset
     */
    void rest(int numberOfCycles);

    /**
     * Generates rules from a natural language sentence
     * <p>
     * NB: Sentences must contain one token from {"if", "when", "while", "first"}, and one token from {"then", "next", "do"},
     *  to denote input facts and output predicates respectively
     *  <p>
     *  e.g. "If Human(near) Then Move(steps=10)"
     *
     * @param sentence that contains input and output delimiters
     */
    void teach(String sentence);
}
