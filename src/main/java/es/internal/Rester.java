package es.internal;

import com.google.inject.assistedinject.Assisted;
import tags.Fact;
import tags.Rule;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

class Rester {
    private Set<Rule> readyRules;

    @Inject
    public Rester(
            @Assisted("readyRules") Set<Rule> readyRules) {
        this.readyRules = readyRules;
    }

    boolean rest(int numberOfCycles) {
        Set<Rule> newRules = mergeCycle(numberOfCycles);
        return readyRules.addAll(newRules);
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
                        if (outputIPredicate.matches(inputFact).isFactMatch()) {
                            Rule mergedRule = new Rule(ruleOne.getInputFacts(), ruleTwo.getOutputPredicates());
                            mergedRules.add(mergedRule);
                            break;
                        }
                    }
                }
            }
        }
    }
}
