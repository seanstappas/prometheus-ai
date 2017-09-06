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
     * @deprecated
     */
    private boolean addTag(Tag tag) {
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
     * Add a Predicate to the ES. Will cast the tag to either a Rule, a Fact.
     *
     * @param predicate the Predicate to be added
     */

    private void addPredicate(IPredicate predicate) {
        switch (predicate.getType()) {
            case FACT:
                addFact((Fact) predicate);
                return;
            case RECOMMENDATION:
                addRecommendation((Recommendation) predicate);
        }
    }

    /**
     * Adds multiple Tags to the ES.
     *
     * @param tags the Tags to be added
     * @deprecated
     */
    public void addTags(Set<Tag> tags) {
        for (Tag t : tags) {
            addTag(t);
        }
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

    /**
     * Removes a fact from the ES.
     * @param fact the Fact to be removed
     * @return <code>true</code> if the ES did not remove the specified Fact
     */

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
     * If inputFact contains a variable argument, matching pair placed in pendingReplacementPairs
     *
     * @param inputFact fact contained in a Rule
     * @return true if (at least) two facts match
     */
    private boolean factsContains(Fact inputFact) {
        boolean result = false;
        for (Fact f : facts) {
            VariableReturn matchesResult = f.matches(inputFact);
            if (matchesResult.isDoesMatch()) {
                if (matchesResult.getPairs().size() > 0) {
                    pendingReplacementPairs.putAll(matchesResult.getPairs());
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * Generates a rule from the facts present in the ES at the beginning of think() and the predicates activated before quiescence
     * <p>
     * Adds generate rule to ES
     *
     * @param inputFactSet Set of Facts in ES
     * @param allActivatedPredicates Set of activated Predicates
     */
    private void generateProvenRule(Set<Fact> inputFactSet, Set<IPredicate> allActivatedPredicates) {
        Set<IPredicate> outputPredicates = new HashSet<>();
        outputPredicates.addAll(allActivatedPredicates);
        Rule provenRule = new Rule(inputFactSet, outputPredicates);
        addRule(provenRule);
    }

    /**
     * Replaces variable argument(s) within a Predicate with a String or Numeric Argument
     *
     * @param predicate the Predicate that has arguments to replace
     */
    private void replaceVariableArguments(IPredicate predicate) {
        int argumentIndex = 0;

        for (Argument argument : predicate.getArguments()) {
            if (pendingReplacementPairs.containsKey(argument.getName())) {
                predicate.getArguments().set(argumentIndex, pendingReplacementPairs.get(argument.getName()));
            }
            argumentIndex++;
        }
    }


    /**
     * {@code shouldGenerateRule} defaults to false
     * @see #think(boolean)
     */

    public Set<Tag> think() {
        Set<IPredicate> allactivatedPredicates = new HashSet<>();
        Set<IPredicate> activatedPredicates;
        do {
            activatedPredicates = thinkCycle();
            allactivatedPredicates.addAll(activatedPredicates);
        } while (!activatedPredicates.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (IPredicate predicate : allactivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        return activatedRecommendations;
    }

    /**
     * Continuously iterates through the read Rules, checking Facts and Recommendations, and activating Rules if
     * possible.
     * <p>
     * Stops once the system reaches natural quiescence and generates a new rule.
     *
     * @param shouldGenerateRule if true generates the new rule proven by a think cycle
     * @return the activated Recommendations as a result of thinking
     */

    public Set<Tag> think(boolean shouldGenerateRule) {
        Set<IPredicate> allactivatedPredicates = new HashSet<>();
        Set<IPredicate> activatedPredicates;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        do {
            activatedPredicates = thinkCycle();
            allactivatedPredicates.addAll(activatedPredicates);
        } while (!activatedPredicates.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (IPredicate predicate : allactivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSetClone, allactivatedPredicates);
        }
        return activatedRecommendations;
    }

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
    public Set<Tag> think(int numberOfCycles, boolean shouldGenerateRule) {
        Set<IPredicate> allactivatedPredicates = new HashSet<>();
        Set<IPredicate> activatedPredicates;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        for (int i = 0; i < numberOfCycles; i++) {
            activatedPredicates = thinkCycle();
            if (activatedPredicates.isEmpty())
                break;
            allactivatedPredicates.addAll(activatedPredicates);
        }
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (IPredicate predicate : allactivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSetClone, allactivatedPredicates);
        }
        return activatedRecommendations;
    }

    /**
     * Makes the ES think for a single cycle.
     * <p>
     * Output predicates of activated rules are replaced if they contain variable arguments e.g. &x
     *
     * @return the activated Predicates as a result of thinking
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
     * Checks if two rules can be merged into a new rule & merges if possible
     * @param inputRule rule 1
     * @param outputRule rule 2
     * @return a merged rule (rule 3)
     */
    private Rule logicReasoner(Rule inputRule, Rule outputRule) {
        for (Fact inputFact : inputRule.getInputFacts()) {
            boolean fullMatch = false;
            for (Fact outputIPredicate : outputRule.getInputFacts()) {
                if (outputIPredicate.matches(inputFact).isDoesMatch()) {
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
     * Iterates over rule set, checks if new merged rules are valid, and repeatedly generates new merged rules from current set of new rules.
     * <p>
     * i.e. rule 1 = A -> B, rule 2 = B -> C, rule 3 = A -> C
     * @param numberOfCycles how many cycles over the rule-set to attempt to merge
     * @return merged rules
     */
    private Set<Rule> ruleMerger(int numberOfCycles) {
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
     * Process that occurs when ES is not thinking.
     * <p>
     * Currently calls addRule to merge rules
     *
     * @param numberOfCycles how many cycles over the ruleset
     */
    public void rest(int numberOfCycles) {
        Set<Rule> newRules = ruleMerger(numberOfCycles);
        for (Rule newRule : newRules) {
            addRule(newRule);
        }
    }

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
            Rule newRule = new Rule(inputFacts, outputFacts);
            addRule(newRule);

        }

        else if (inputIndex > outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex+1, tokenList.size()).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, inputIndex).toArray(new String[0]);
            Rule newRule = new Rule(inputFacts, outputFacts);
            addRule(newRule);
        }
    }

}