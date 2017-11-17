package es.internal;

import tags.Fact;
import tags.Predicate;
import tags.Rule;

import java.util.Optional;
import java.util.Set;

class RuleMerger {
    Optional<Rule> makeMergedRule(Set<Rule> rules) {
        for (Rule ruleOne : rules) {
            for (Rule ruleTwo : rules) {
                if (ruleOne != ruleTwo) {
                    boolean match = true;
                    for (Fact inputFact : ruleOne.getInputFacts()) {
                        if (match) {
                            for (Predicate outputIPredicate : ruleTwo.getOutputPredicates()) {
                                if (!(outputIPredicate instanceof Fact) ||
                                        !((Fact)outputIPredicate).matches(inputFact)) {
                                    match = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        Rule mergedRule = new Rule(ruleTwo.getInputFacts(), ruleOne.getOutputPredicates());
                        return Optional.of(mergedRule);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
