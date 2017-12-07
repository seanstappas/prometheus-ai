package es.internal;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

/**
 * Teacher which creates Rules from natural language sentences.
 */
class Teacher {
    private static final Set<String> INPUT_TOKENS =
            new HashSet<>(Arrays.asList("if", "when", "while", "first"));
    private static final Set<String> OUTPUT_TOKENS =
            new HashSet<>(Arrays.asList("then", "next", "do"));
    private final Set<Rule> readyRules;

    @Inject
    Teacher(
            @Assisted("readyRules") final Set<Rule> readyRules) {
        this.readyRules = readyRules;
    }


    /**
     * Creates a Rule from the given natural language sentence.
     *
     * @param sentence the sentence to learn from
     */
    void teach(final String sentence) {
        final String[] tokens = sentence.split("\\s");
        final List<String> tokenList = new ArrayList<>();
        tokenList.addAll(Arrays.asList(tokens));
        final int[] ruleIndices = findRuleIndices(tokenList);
        makeTaughtRule(tokenList, ruleIndices[0], ruleIndices[1])
                .ifPresent(readyRules::add);
    }

    /**
     * Finds the indices of the input and output tokens in the given token list.
     *
     * @param tokenList the token list to find indices from
     * @return the indices of the input and output tokens in the given token
     * list, respectively.
     */
    private int[] findRuleIndices(final List<String> tokenList) {
        int inputIndex = -1;
        int outputIndex = -1;

        for (final String token : tokenList) {
            if (INPUT_TOKENS.contains(token.toLowerCase())) {
                inputIndex = tokenList.indexOf(token);
            } else if (OUTPUT_TOKENS.contains(token.toLowerCase())) {
                outputIndex = tokenList.indexOf(token);
            }
        }

        return new int[] {inputIndex, outputIndex};
    }

    /**
     * Makes a taught rule from the given token list and indices of the input
     * and output tokens.
     *
     * @param tokenList   the token list
     * @param inputIndex  the index of the input token
     * @param outputIndex the index of the output token
     * @return an Optional object containing the taught rule if the given
     * indices are valid, otherwise an empty Optional object
     */
    private Optional<Rule> makeTaughtRule(final List<String> tokenList,
                                          final int inputIndex,
                                          final int outputIndex) {
        if (inputIndex < outputIndex) {
            final String[] inputFacts =
                    tokenList.subList(inputIndex + 1, outputIndex)
                            .toArray(new String[0]);
            final String[] outputFacts =
                    tokenList.subList(outputIndex + 1, tokenList.size())
                            .toArray(new String[0]);
            return Optional.of(new Rule(inputFacts, outputFacts));
        } else if (inputIndex > outputIndex) {
            final String[] inputFacts =
                    tokenList.subList(inputIndex + 1, tokenList.size())
                            .toArray(new String[0]);
            final String[] outputFacts =
                    tokenList.subList(outputIndex + 1, inputIndex)
                            .toArray(new String[0]);
            return Optional.of(new Rule(inputFacts, outputFacts));
        }
        return Optional.empty();
    }
}
