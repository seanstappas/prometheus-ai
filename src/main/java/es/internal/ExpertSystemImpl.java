package es.internal;

import es.api.ExpertSystem;
import tags.*;

import javax.inject.Inject;
import java.util.*;

/**
 * Expert System (ES)
 */
class ExpertSystemImpl implements ExpertSystem {
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;

    /**
     * Creates an Expert System (ES).
     */
    @Inject
    public ExpertSystemImpl() {
        readyRules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
        activeRules = new HashSet<>();
    }

    @Override
    public void reset() {
        activeRules.clear();
        readyRules.clear();
        facts.clear();
        recommendations.clear();
    }

    @Override
    public void deactivateRules() {
        readyRules.addAll(activeRules);
        activeRules.clear();
    }

    @Override
    public void addTags(Set<Tag> tags) {
        for (Tag t : tags) {
            addTag(t);
        }
    }

    @Override
    public boolean addFact(Fact fact) {
        return facts.add(fact);
    }

    @Override
    public boolean removeFact(Fact fact) {
        return facts.remove(fact);
    }

    @Override
    public boolean addRule(Rule rule) {
        return readyRules.add(rule);
    }

    @Override
    public boolean addRecommendation(Recommendation rec) {
        return recommendations.add(rec);
    }

    @Override
    public Set<Recommendation> getRecommendations() {
        return recommendations;
    }

    @Override
    public Set<Rule> getReadyRules() { // For testing purposes
        return readyRules;
    }

    @Override
    public Set<Rule> getActiveRules() { // For testing purposes
        return activeRules;
    }

    @Override
    public Set<Fact> getFacts() { // For testing purposes
        return facts;
    }

    @Override
    public Set<Tag> think() {
        Set<Predicate> allActivatedPredicates = new HashSet<>();
        Set<Predicate> activatedPredicates;
        do {
            activatedPredicates = thinkCycle();
            allActivatedPredicates.addAll(activatedPredicates);
        } while (!activatedPredicates.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Predicate predicate : allActivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        return activatedRecommendations;
    }

    @Override
    public Set<Tag> think(boolean shouldGenerateRule) {
        Set<Predicate> allActivatedPredicates = new HashSet<>();
        Set<Predicate> activatedPredicates;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        do {
            activatedPredicates = thinkCycle();
            allActivatedPredicates.addAll(activatedPredicates);
        } while (!activatedPredicates.isEmpty());
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Predicate predicate : allActivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSetClone, allActivatedPredicates);
        }
        return activatedRecommendations;
    }

    @Override
    public Set<Tag> think(int numberOfCycles, boolean shouldGenerateRule) {
        Set<Predicate> allactivatedPredicates = new HashSet<>();
        Set<Predicate> activatedPredicates;
        Set<Fact> inputFactSet = getFacts();
        Set<Fact> inputFactSetClone = new HashSet<>(inputFactSet);
        for (int i = 0; i < numberOfCycles; i++) {
            activatedPredicates = thinkCycle();
            if (activatedPredicates.isEmpty())
                break;
            allactivatedPredicates.addAll(activatedPredicates);
        }
        Set<Tag> activatedRecommendations = new HashSet<>();
        for (Predicate predicate : allactivatedPredicates) {
            if (predicate instanceof Recommendation)
                activatedRecommendations.add((Recommendation) predicate);
        }
        if (shouldGenerateRule) {
            generateProvenRule(inputFactSetClone, allactivatedPredicates);
        }
        return activatedRecommendations;
    }

    @Override
    public void rest(int numberOfCycles) {
        Set<Rule> newRules = mergeCycle(numberOfCycles);
        for (Rule newRule : newRules) {
            addRule(newRule);
        }
    }

    @Override
    public void teach(String sentence) {
        String[] tokens = sentence.split("\\s");
        List<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            tokenList.add(token.toLowerCase());
        }

        int ruleIndices[] = findRuleIndices(tokenList);
        Rule taughtRule = makeTaughtRule(tokenList, ruleIndices[0], ruleIndices[1]);

        addRule(taughtRule);
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

    private void addPredicate(Predicate predicate) {
        switch (predicate.getType()) {
            case FACT:
                addFact((Fact) predicate);
                return;
            case RECOMMENDATION:
                addRecommendation((Recommendation) predicate);
        }
    }

    /**
     * Checks if a particular fact matches with any other fact in the ES
     * If inputFact contains a variable argument, matching pair placed in pendingReplacementPairs
     *
     * @param inputFact fact contained in a Rule
     * @return true if (at least) two facts match
     */
    private boolean factsContains(Fact inputFact, Map<String, Argument> pendingReplacementPairs) {
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
    private void generateProvenRule(Set<Fact> inputFactSet, Set<Predicate> allActivatedPredicates) {
        Set<Predicate> outputPredicates = new HashSet<>();
        outputPredicates.addAll(allActivatedPredicates);
        Rule provenRule = new Rule(inputFactSet, outputPredicates);
        addRule(provenRule);
    }

    /**
     * Replaces variable argument(s) within a Predicate with a String or Numeric Argument
     *
     * @param predicate the Predicate that has arguments to replace
     */
    private void replaceVariableArguments(Predicate predicate, Map<String, Argument> pendingReplacementPairs) {
        int argumentIndex = 0;

        for (Argument argument : predicate.getArguments()) {
            if (pendingReplacementPairs.containsKey(argument.getName())) {
                predicate.getArguments().set(argumentIndex, pendingReplacementPairs.get(argument.getName()));
            }
            argumentIndex++;
        }
    }

    /**
     * Makes the ES think for a single cycle.
     * <p>
     * Output predicates of activated rules are replaced if they contain variable arguments e.g. &x
     *
     * @return the activated Predicates as a result of thinking
     */
    private Set<Predicate> thinkCycle() {
        Set<Predicate> activatedPredicates = new HashSet<>();
        Set<Rule> pendingActivatedRules = new HashSet<>();
        Map<String, Argument> pendingReplacementPairs = new HashMap<>();
        for (Rule rule : readyRules) {
            boolean shouldActivate = true;
            for (Fact fact : rule.getInputFacts()) {
                if (!factsContains(fact, pendingReplacementPairs)) {
                    shouldActivate = false;
                    break;
                }
            }
            if (shouldActivate) {
                pendingActivatedRules.add(rule);
            }
        }
        activateRules(activatedPredicates, pendingActivatedRules, pendingReplacementPairs);
        return activatedPredicates;
    }

    private void activateRules(Set<Predicate> activatedPredicates, Set<Rule> pendingActivatedRules, Map<String, Argument> pendingReplacementPairs) {
        for (Rule rule : pendingActivatedRules) {
            readyRules.remove(rule);
            activeRules.add(rule);
            for (Predicate predicate : rule.getOutputPredicates()) {
                replaceVariableArguments(predicate, pendingReplacementPairs);
                activatedPredicates.add(predicate);
                addPredicate(predicate);
            }
        }
    }

    /**
     * Checks if two rules can be merged into a new rule & merges if possible
     * @param ruleOne rule 1
     * @param ruleTwo rule 2
     * @return a merged rule (rule 3)
     */
    private Rule logicReasoner(Rule ruleOne, Rule ruleTwo) {

        return new Rule(ruleOne.getInputFacts(), ruleTwo.getOutputPredicates());
    }

    /**
     * Iterates over rule set, checks if new merged rules are valid, and repeatedly generates new merged rules from current set of new rules.
     * <p>
     * i.e. rule 1 = A -> B, rule 2 = B -> C, rule 3 = A -> C
     * @param numberOfCycles how many cycles over the rule-set to attempt to merge
     * @return merged rules
     */
    private Set<Rule> mergeCycle(int numberOfCycles) {
        Set<Rule> mergedRules = new HashSet<>();
        Set<Rule> inputRules = new HashSet<>();
        inputRules.addAll(readyRules);
        while (numberOfCycles > 0) {
            makeMergedRule(mergedRules, inputRules);
            numberOfCycles--;
            inputRules = mergedRules;
        }
        return mergedRules;
    }

    private void makeMergedRule(Set<Rule> mergedRules, Set<Rule> inputRules) {
        for (Rule ruleOne : inputRules) {
            for (Rule ruleTwo : inputRules) {
                for (Fact inputFact : ruleOne.getInputFacts()) {
                    for (Fact outputIPredicate : ruleTwo.getInputFacts()) {
                        if (outputIPredicate.matches(inputFact).isDoesMatch()) {
                            Rule mergedRule = new Rule(ruleOne.getInputFacts(), ruleTwo.getOutputPredicates());
                            mergedRules.add(mergedRule);
                            break;
                        }
                    }
                }
            }
        }
    }

    private int[] findRuleIndices(List<String> tokenList) {
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
        return new int[]{inputIndex, outputIndex};
    }

    private Rule makeTaughtRule(List<String> tokenList, int inputIndex, int outputIndex) {
        Rule newRule = new Rule();
        if (inputIndex < outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex + 1, outputIndex).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, tokenList.size()).toArray(new String[0]);
            newRule = new Rule(inputFacts, outputFacts);
        } else if (inputIndex > outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex + 1, tokenList.size()).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, inputIndex).toArray(new String[0]);
            newRule = new Rule(inputFacts, outputFacts);
        }
        return newRule;
    }

}