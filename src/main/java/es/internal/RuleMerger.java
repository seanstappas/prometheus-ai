package es.internal;

import java.util.Optional;
import java.util.Set;
import tags.Fact;
import tags.Predicate;
import tags.Rule;

/**
 * Merges rules.
 */
class RuleMerger {
    /**
     * Creates a merged Rule from the given Set of Rules.
     *
     * @param rules the rules to find a Rule-pair within.
     * @return a merged Rule if one was found, else an empty Optional object
     */
    Optional<Rule> makeMergedRule(final Set<Rule> rules) {
        for (final Rule ruleOne : rules) {
            for (final Rule ruleTwo : rules) {
                if (ruleOne != ruleTwo) {
                    boolean match = true;
                    for (final Fact inputFact : ruleOne.getInputFacts()) {
                        if (match) {
                            for (final Predicate outputIPredicate : ruleTwo
                                    .getOutputPredicates()) {
                                if (!(outputIPredicate instanceof Fact)
                                        || !((Fact) outputIPredicate)
                                        .matches(inputFact)) {
                                    match = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        final Rule mergedRule =
                                new Rule(ruleTwo.getInputFacts(),
                                        ruleOne.getOutputPredicates());
                        return Optional.of(mergedRule);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
