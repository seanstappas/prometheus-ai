package es.internal;

import com.google.inject.assistedinject.Assisted;
import tags.Rule;

import javax.inject.Inject;
import java.util.*;

class Teacher {
    private Set<Rule> readyRules;

    @Inject
    public Teacher(
            @Assisted("readyRules") Set<Rule> readyRules) {
        this.readyRules = readyRules;
    }

    void teach(String sentence) {
        String[] tokens = sentence.split("\\s");
        List<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            tokenList.add(token.toLowerCase());
        }
        int ruleIndices[] = findRuleIndices(tokenList);
        makeTaughtRule(tokenList, ruleIndices[0], ruleIndices[1]).ifPresent(readyRules::add);
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
