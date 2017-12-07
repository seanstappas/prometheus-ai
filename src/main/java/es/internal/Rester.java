package es.internal;

import javax.inject.Inject;
import java.util.Set;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

/**
 * Rests the ES, generating merged rules.
 */
class Rester {
    private final Set<Rule> readyRules;
    private final RuleMerger ruleMerger;

    @Inject
    Rester(
            @Assisted("readyRules") final Set<Rule> readyRules,
            final RuleMerger ruleMerger) {
        this.readyRules = readyRules;
        this.ruleMerger = ruleMerger;
    }

    /**
     * Iterates over rule set, checks if new merged rules are valid, and
     * repeatedly generates new merged rules from current set of new rules.
     * <p>
     * i.e. rule 1 = A -> B, rule 2 = B -> C, rule 3 = A -> C
     *
     * @param numberOfCycles how many cycles over the rule-set to attempt to
     *                       merge
     */
    void rest(final int numberOfCycles) {
        for (int i = 0; i < numberOfCycles; i++) {
            ruleMerger.makeMergedRule(readyRules).ifPresent(readyRules::add);
        }
    }
}
