package es.internal;

import tags.Fact;
import tags.Rule;

import java.util.Optional;
import java.util.Set;

class RuleMerger {
    Optional<Rule> makeMergedRule(Set<Rule> inputRules) {
        for (Rule ruleOne : inputRules) {
            for (Rule ruleTwo : inputRules) {
                for (Fact inputFact : ruleOne.getInputFacts()) {
                    for (Fact outputIPredicate : ruleTwo.getInputFacts()) {
                        if (outputIPredicate.matches(inputFact).isFactMatch()) {
                            Rule mergedRule = new Rule(ruleOne.getInputFacts(), ruleTwo.getOutputPredicates());
                            return Optional.of(mergedRule);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
