package es;

import interfaces.PrometheusLayer;
import tags.*;

import java.util.*;

/**
 * Expert System (ES)
 */
public class ExpertSystem implements PrometheusLayer {
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;
    private Map<String, Argument> pendingReplacementPairs;


    /**
     * Creates an Expert System (ES).
     */
    public ExpertSystem() {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activeRules = new HashSet<>();
        pendingReplacementPairs = new HashMap<>();
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
     * @param tag the Tag to be added
     * @return <code>true</code> if the Tag is successfully added
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

    public boolean addPredicate(IPredicate predicate) {
        switch (predicate.getType()) {
            case FACT:
                return addFact((Fact) predicate);
            case RECOMMENDATION:
                return addRecommendation((Recommendation) predicate);
        }
        return false;
    }

    /**
     * Adds multiple Tags to the ES.
     *
     * @param tags the Tags to be added
     * @return <code>true</code> if all the Tags are added successfully
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
     * @param fact the Fact to be added
     * @return <code>true</code> if the ES did not already contain the specified Fact
     */
    public boolean addFact(Fact fact) {
        return facts.add(fact);
    }

    public boolean removeFact(Fact fact) {
        return facts.remove(fact);
    }

    /**
     * Adds a Rule to the ES.
     *
     * @param rule the Rule to be added
     * @return <code>true</code> if the ES did not already contain the specified Rule
     */
    public boolean addRule(Rule rule) {
        return readyRules.add(rule);
    }

    /**
     * Adds a Recommendation to the ES.
     *
     * @param rec the Recommendation to be added
     * @return <code>true</code> if the ES did not already contain the specified Recommendation
     */
    public boolean addRecommendation(Recommendation rec) {
        return recommendations.add(rec);
    }

    /**
     * Gets all the Recommendations of the ES.
     *
     * @return the Recommendations of the ES
     */
    public Set<Recommendation> getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the ready Rules of the ES.
     *
     * @return the ready Rules of the ES
     */
    public Set<Rule> getReadyRules() { // For testing purposes
        return readyRules;
    }

    /**
     * Gets the active Rules of the ES.
     *
     * @return the active Rules of the ES
     */
    public Set<Rule> getActiveRules() { // For testing purposes
        return activeRules;
    }

    /**
     * Gets the Facts of the ES.
     *
     * @return the Facts of the ES
     */
    public Set<Fact> getFacts() { // For testing purposes
        return facts;
    }

    /**
     * Checks if a particular fact matches with any other fact in the ES
     * @param inputFact
     * @return true if (at least) two facts match
     */
    public boolean factsContains(Fact inputFact) {
        boolean result = false;
        for (Fact f : facts) {
            VariableReturn matchesResult = f.matches(inputFact);
            if (matchesResult.doesMatch) {
                if (matchesResult.pairs.size() > 0) {
                    pendingReplacementPairs.putAll(matchesResult.pairs);
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * Generates a rule from the facts present in the ES at the beginning of think() and the tags activated before quiescence
     * @param inputFactSet facts in ES
     * @param allActivatedPredicates activated predicates
     */
    private void generateProvenRule(Set<Fact> inputFactSet, Set<IPredicate> allActivatedPredicates) {
        Set<IPredicate> outputPredicates = new HashSet<>();
        outputPredicates.addAll(allActivatedPredicates);
        Rule provenRule = new Rule(inputFactSet, outputPredicates);
        addRule(provenRule);
    }

    /**
     * Replaces variable argument(s) within a tag with a String or Numeric Argument
     *
     * @param tag the tag with arguments to replace
     */
    private void replaceVariableArguments(IPredicate tag) {
        int argumentIndex = 0;

        for (Argument argument : tag.getArguments()) {
            if (pendingReplacementPairs.containsKey(argument.getName())) {
                tag.getArguments().set(argumentIndex, pendingReplacementPairs.get(argument.getName()));
            }
            argumentIndex++;
        }
    }


    /**
     * Makes the ES think for a fixed number of cycles. The number of cycles represents how much effort is being put
     * into thinking. Each cycle is a run-through of all the ready Rules, activating Rules if possible. Note that a Rule
     * that is activated in a cycle is not iterated over in that same cycle, and must wait until the next cycle to
     * cascade further activation. This is threshold quiescence, which may or may not correspond with natural
     * quiescence.
     *
     * @return the activated Recommendations as a result of thinking
     */
    public Set<Tag> think() {
        Set<IPredicate> allActivatedTags = new HashSet<>();
        Set<IPredicate> activatedTags;
        do {
            activatedTags = thinkCycle();
            allActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (IPredicate predicate : allActivatedTags) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
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
     * @return the activated Recommendations as a result of thinking
     */
    public Set<Tag> think(boolean shouldGenerateRule) {
        Set<IPredicate> allActivatedTags = new HashSet<>();
        Set<IPredicate> activatedTags;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        do {
            activatedTags = thinkCycle();
            allActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (IPredicate predicate : allActivatedTags) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSetClone, allActivatedTags);
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
     * @param numberOfCycles the number of cycles to think for
     * @param shouldGenerateRule if true generates the new rule proven by a think cycle
     * @return the activated Recommendations as a result of thinking
     */
    public Set<Tag> think(int numberOfCycles, boolean shouldGenerateRule) {
        Set<IPredicate> allActivatedTags = new HashSet<>();
        Set<IPredicate> activatedTags;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        for (int i = 0; i < numberOfCycles; i++) {
            activatedTags = thinkCycle();
            if (activatedTags.isEmpty())
                break;
            allActivatedTags.addAll(activatedTags);
        }
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (IPredicate predicate : allActivatedTags) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSetClone, allActivatedTags);
        }
        return activatedRecommendations;
    }

    /**
     * Makes the ES think for a single cycle.
     *
     * @return the activated Tags as a result of thinking
     */
    private Set<IPredicate> thinkCycle() {
        Set<IPredicate> activatedPredicates = new HashSet<>();
        Set<Rule> pendingActivatedRules = new HashSet<>();
        for (Rule rule : readyRules) {
            boolean shouldActivate = true;
            for (Fact fact : rule.getInputFacts()) {
                if (!factsContains(fact)) {
                    shouldActivate = false;
                    break;
                }
            }
            if (shouldActivate) {
                pendingActivatedRules.add(rule);
            }
        }
        for (Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            activeRules.add(rule);
            for (IPredicate predicate : rule.getOutputPredicates()) {
                replaceVariableArguments(predicate);
                activatedPredicates.add(predicate);
                addPredicate(predicate);
            }
        }
        return activatedPredicates;
    }


    /**
     * Checks if two rules can be merged into a new rule
     * i.e. rule 1 = A -> B, rule 2 = B -> C, rule 3 = A -> C
     * @param inputRule rule 1
     * @param outputRule rule 2
     * @return a merged rule (rule 3)
     */
    private Rule logicReasoner(Rule inputRule, Rule outputRule) {
        for (Fact inputFact : inputRule.getInputFacts()) {
            boolean fullMatch = false;
            for (Fact outputIPredicate : outputRule.getInputFacts()) {
                if (outputIPredicate.matches(inputFact).doesMatch) {
                    fullMatch = true;
                    break;
                }
            }
            if (!fullMatch) {
                return new Rule();
            }
        }
        return new Rule(inputRule.getInputFacts(), outputRule.getOutputPredicates());
    }

    /**
     * Iterates over rule set, checks if new merged rules are valid, and repeatedly generates new merged rules from current set of new rules
     * @param numberOfCycles how many cycles over the ruleset
     * @return merged rules
     */
    Set<Rule> ruleMerger(int numberOfCycles) {
        Set<Rule> mergedRules = new HashSet<>();
        Set<Rule> inputRules = new HashSet<>();
        inputRules.addAll(readyRules);
        while (numberOfCycles > 0) {
            for (Rule ruleOne : inputRules) {
                for (Rule ruleTwo : inputRules) {
                    Rule mergedRule = logicReasoner(ruleOne, ruleTwo);
                    if (!mergedRule.equals(new Rule())) {
                        mergedRules.add(mergedRule);
                    }
                }
            }
            numberOfCycles--;
            inputRules = mergedRules;
        }
        return mergedRules;
    }

    /**
     * Process that occurs when ES is not thinking
     * Currently calls addRule to merge rules
     *
     * @param numberOfCycles how many cycles over the ruleset
     */
    public void rest(int numberOfCycles) {
        Set<Rule> newRules = ruleMerger(1);
        for (Rule newRule : newRules) {
            addRule(newRule);
        }
    }

    /**
     * Generates rules from a natural language sentence
     * NB: Sentences must contain one token from {"if", "when", "while", "first"}, and one token from {"then", "next", "do"},
     *  to denote input tags and output tags respectively
     *  e.g. "If Human(near) Then Move(steps=10)"
     * @param sentence that contains input and output delimiters
     */

    public void teach(String sentence) {
        String[] tokens = sentence.split("\\s");
        List<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            tokenList.add(token.toLowerCase());
        }

        int inputIndex = -1;
        String [] inputTokens = {"if", "when", "while", "first"};
        for (String inputToken : inputTokens) {
            if (tokenList.contains(inputToken)) {
                inputIndex = tokenList.indexOf(inputToken);
                break;
            }
        }

        int outputIndex = -1;
        String [] outputTokens = {"then", "next", "do"};
        for (String outputToken : outputTokens) {
            if (tokenList.contains(outputToken)) {
                outputIndex = tokenList.indexOf(outputToken);
                break;
            }
        }

        if (inputIndex < outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex+1, outputIndex).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, tokenList.size()).toArray(new String[0]);
            Rule newRule = new Rule(inputFacts, outputFacts, Tag.TagType.RULE);
            addRule(newRule);

        }

        else if (inputIndex > outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex+1, tokenList.size()).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, inputIndex).toArray(new String[0]);
            Rule newRule = new Rule(inputFacts, outputFacts, Tag.TagType.RULE);
            addRule(newRule);
        }
    }

}