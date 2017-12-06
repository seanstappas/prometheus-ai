package es.internal;

import com.google.inject.assistedinject.Assisted;
import tags.Rule;

import javax.inject.Inject;
import java.util.*;

class Teacher {
    private static final Set<String> INPUT_TOKENS = new HashSet<>(Arrays.asList("if", "when", "while", "first"));
    private static final Set<String> OUTPUT_TOKENS = new HashSet<>(Arrays.asList("then", "next", "do"));
    private Set<Rule> readyRules;

    @Inject
    public Teacher(
            @Assisted("readyRules") Set<Rule> readyRules) {
        this.readyRules = readyRules;
    }


    /**
     * Creates a Rule from the given natural language sentence.
     *
     * @param sentence the sentence to learn from
     */
    void teach(String sentence) {
        String[] tokens = sentence.split("\\s");
        List<String> tokenList = new ArrayList<>();
        tokenList.addAll(Arrays.asList(tokens));
        int ruleIndices[] = findRuleIndices(tokenList);
        makeTaughtRule(tokenList, ruleIndices[0], ruleIndices[1]).ifPresent(readyRules::add);
    }

    /**
     * Finds the indices of the input and output tokens in the given token list.
     *
     * @param tokenList the token list to find indices from
     * @return the indices of the input and output tokens in the given token list, respectively.
     */
    private int[] findRuleIndices(List<String> tokenList) {
        int inputIndex = -1;
        int outputIndex = -1;

        for (String token : tokenList) {
            if (INPUT_TOKENS.contains(token.toLowerCase())) {
                inputIndex = tokenList.indexOf(token);
            } else if (OUTPUT_TOKENS.contains(token.toLowerCase())) {
                outputIndex = tokenList.indexOf(token);
            }
        }

        return new int[]{inputIndex, outputIndex};
    }

    /**
     * Makes a taught rule from the given token list and indices of the input and output tokens.
     *
     * @param tokenList   the token list
     * @param inputIndex  the index of the input token
     * @param outputIndex the index of the output token
     * @return an Optional object containing the taught rule if the given indices are valid, otherwise an empty Optional
     * object
     */
    private Optional<Rule> makeTaughtRule(List<String> tokenList, int inputIndex, int outputIndex) {
        if (inputIndex < outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex + 1, outputIndex).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, tokenList.size()).toArray(new String[0]);
            return Optional.of(new Rule(inputFacts, outputFacts));
        } else if (inputIndex > outputIndex) {
            String[] inputFacts = tokenList.subList(inputIndex + 1, tokenList.size()).toArray(new String[0]);
            String[] outputFacts = tokenList.subList(outputIndex + 1, inputIndex).toArray(new String[0]);
            return Optional.of(new Rule(inputFacts, outputFacts));
        }
        return Optional.empty();
    }
}
