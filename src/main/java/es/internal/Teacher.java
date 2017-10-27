package es.internal;

import com.google.inject.assistedinject.Assisted;
import tags.Rule;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Teacher {
    private Set<Rule> readyRules;

    @Inject
    public Teacher(
            @Assisted("readyRules") Set<Rule> readyRules) {
        this.readyRules = readyRules;
    }

    boolean teach(String sentence) {
        String[] tokens = sentence.split("\\s");
        List<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            tokenList.add(token.toLowerCase());
        }
        int ruleIndices[] = findRuleIndices(tokenList);
        Rule taughtRule = makeTaughtRule(tokenList, ruleIndices[0], ruleIndices[1]);
        return readyRules.add(taughtRule);
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
