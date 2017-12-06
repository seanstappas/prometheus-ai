package es.internal;

import javax.inject.Inject;
import java.util.Set;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

class Rester {
    private Set<Rule> readyRules;
    private RuleMerger ruleMerger;

    @Inject
    public Rester(
            @Assisted("readyRules") Set<Rule> readyRules,
            RuleMerger ruleMerger) {
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
    void rest(int numberOfCycles) {
        for (int i = 0; i < numberOfCycles; i++) {
            ruleMerger.makeMergedRule(readyRules).ifPresent(readyRules::add);
        }
    }
}
