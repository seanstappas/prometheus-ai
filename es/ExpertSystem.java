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
     * Continuously iterates through the read Rules, checking Facts and Recommendations, and activating Rules if
     * possible. Stops once the system reaches natural quiescence and generates rule.
     *
     * @return the activated Recommendations as a result of thinking
     *
     */
    public Set<Tag> think() {
        Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> activatedTags;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        do {
            activatedTags = thinkCycle();
            allActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Tag tag : allActivatedTags) {
            if (tag.isRecommendation())
                activatedRecommendations.add(tag);
        }
        generateProvenRule(inputFactSetClone, allActivatedTags);
        return activatedRecommendations;
    }


    /**
     * Continuously iterates through the read Rules, checking Facts and Recommendations, and activating Rules if
     * possible. Stops once the system reaches natural quiescence.
     *
     * @param shouldGenerateRule if true generates the new rule proven by a think cycle
     * @return the activated Recommendations as a result of thinking
     */
    public Set<Tag> think(boolean shouldGenerateRule) {
        Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> activatedTags;
        Set<Fact> inputFactSet = getFacts();
        do {
            activatedTags = thinkCycle();
            allActivatedTags.addAll(activatedTags);
        } while (!activatedTags.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Tag tag : allActivatedTags) {
            if (tag.isRecommendation())
                activatedRecommendations.add(tag);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSet, allActivatedTags);
        }
        return activatedRecommendations;
    }


    /**
     * Generates a rule from the facts present in the ES at the beginning of think() and the tags activated before quiescence
     * @param inputFactSet facts in ES
     * @param allActivatedTags activated tags
     */
    private void generateProvenRule(Set<Fact> inputFactSet, Set<Tag> allActivatedTags) {
        Fact[] inputFacts = inputFactSet.toArray(new Fact[inputFactSet.size()]);
        Fact[] outputTags = allActivatedTags.toArray(new Fact[allActivatedTags.size()]);
        Rule provenRule = new Rule(inputFacts, outputTags);
        addRule(provenRule);
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
        Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> activatedTags;
        Set<Fact> inputFactSet = getFacts();
        for (int i = 0; i < numberOfCycles; i++) {
            activatedTags = thinkCycle();
            if (activatedTags.isEmpty())
                break;
            allActivatedTags.addAll(activatedTags);
        }
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Tag tag : allActivatedTags) {
            if (tag.isRecommendation())
                activatedRecommendations.add(tag);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSet, allActivatedTags);
        }
        return activatedRecommendations;
    }

    /**
     * Makes the ES think for a single cycle.
     *
     * @return the activated Tags as a result of thinking
     */
    private Set<Tag> thinkCycle() {
        Set<Tag> activatedTags = new HashSet<>();
        Set<Rule> pendingActivatedRules = new HashSet<>();
        for (Rule rule : readyRules) {
            boolean shouldActivate = true;
            for (Fact fact : rule.inputFacts) {
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
            for (Tag tag : rule.outputTags) {
                replaceVariableArguments((Fact) tag);
                activatedTags.add(tag);
                addTag(tag);
            }
        }
        return activatedTags;
    }

    /**
     * Replaces variable argument(s) within a tag with a String or Numeric Argument
     * @param tag the tag with arguments to replace
     */
    private void replaceVariableArguments(Fact tag) {
        int argumentIndex = 0;

        Set<Integer> toRemove = new HashSet<>();
        Set<Argument> toAdd = new HashSet<>();

        System.out.println("BEFORE: " + tag.getArguments());

        for (Argument argument : tag.getArguments()) {
            if (pendingReplacementPairs.containsKey(argument.getName())) {
//                toRemove.add(argumentIndex);
//                toAdd.add(pendingReplacementPairs.get(argument.getName()));
                tag.getArguments().set(argumentIndex, pendingReplacementPairs.get(argument.getName()));
            }
            argumentIndex++;
        }

//        for (int arg : toRemove) {
//            tag.getArguments().remove(arg);
//        }
//        for (Argument arg : toAdd) {
//            tag.getArguments().add(arg);
//        }

        System.out.println("AFTER: " + tag.getArguments());
    }

    /**
     * Checks if two rules can be merged into a new rule
     * i.e. rule 1 = A -> B, rule 2 = B -> C, rule 3 = A -> C
     * @param inputRule rule 1
     * @param outputRule rule 2
     * @return a merged rule (rule 3)
     */
    private Rule logicReasoner(Rule inputRule, Rule outputRule) {
        for (Fact inputFact : inputRule.getOutputTags()) {
            boolean fullMatch = false;
            for (Fact outputFact : outputRule.getInputFacts()) {
                if (outputFact.matches(inputFact).doesMatch) {
                    fullMatch = true;
                    break;
                }
            }
            if (!fullMatch) {
                return new Rule();
            }
        }
        return new Rule(inputRule.inputFacts, outputRule.outputTags);
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
     * @return true if new rule added
     */

    public boolean teach(String sentence) {
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
            String[] outputTags = tokenList.subList(outputIndex + 1, tokenList.size()).toArray(new String[0]);
            Rule newRule = new Rule(inputFacts, outputTags, Tag.TagType.RULE);
            return addRule(newRule);

        }

        else if (inputIndex > outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex+1, tokenList.size()).toArray(new String[0]);
            String[] outputTags = tokenList.subList(outputIndex + 1, inputIndex).toArray(new String[0]);
            Rule newRule = new Rule(inputFacts, outputTags, Tag.TagType.RULE);
            return addRule(newRule);
        }

        return false;
    }

}