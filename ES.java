import java.util.HashSet;
import java.util.Set;

/**
 * Expert System
 */
public class ES {
    private Set<Rule> rules; // TODO: HashSet OK? instead of HashMap
    private Set<String> facts; // TODO: HashSet OK? instead of ArrayList
    // each predicate as String: '(Px)'
    private Set<String> recommendations; // TODO: HashSet OK? instead of ArrayList
    // Type of recommendations? Good. Recommendations are for specific actions to be taken (walk, stop...). These recommendations are passed up the chain
    // Recommendations are indicated by some special symbol: (#...)

    public ES() {
        rules = new HashSet<>();
        facts = new HashSet<>();
        recommendations = new HashSet<>();
//        reset(); // TODO: Why call reset?
    }

    public void reset() {
        rules.clear();
        facts.clear();
        recommendations.clear();
    }

    public void addFact(String fact) { // TODO: Fact is a String? Could have String constructor..
        facts.add(fact);
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }

    private void addRecommendation(String rec) {
        recommendations.add(rec);
    }

    public Set getRecommendations() {
        return recommendations;
    }

    /**
     * 1. Iterate through rules, checking facts and activating if applicable.
     * 2. Repeat until no activations in a cycle.
     */
    public void think() { // TODO: Complete think() method: set rules from KNN, compare Facts and Rules: populate Rules, Facts
        boolean fired;
        do {
            fired = false;
            for (Rule rule : rules) {
                if (!rule.activated && (!facts.contains(rule.action) || (isRecommendation(rule.action) && !recommendations.contains(rule.action)))) {
                    boolean shouldActivate = true;
                    for (String condition : rule.conditions) {
                        if (!facts.contains(condition)) {
                            shouldActivate = false;
                        }
                    }
                    if (shouldActivate) {
                        rule.activated = true;
                        fired = true;
                        if (isRecommendation(rule.action))
                            recommendations.add(rule.action);
                        else
                            facts.add(rule.action);
                    }
                }
            }
        } while (fired);
    }


    class Rule {
        Set<String> conditions; // TODO: Is this change to set OK?
        String action;
        boolean activated; // Check if rule should be considered

        public Rule(Set<String> conditions, String action, boolean activated) {
            this.conditions = conditions;
            this.action = action;
            this.activated = activated;
        }
    }

    boolean isRecommendation(String action) {
        return action.charAt(1) == '#';
    }
}
