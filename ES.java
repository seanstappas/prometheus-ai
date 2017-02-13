import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Expert System
 */
public class ES {
    private Set<Rule> rules; // TODO: HashSet OK? instead of HashMap. In my opinion, it makes more sense to iterate first over the Rules, and check the facts for each rule
    private Set<String> facts; // TODO: HashSet OK? instead of ArrayList. Iteration order important? HashSet has O(1) for contains() method
    // each predicate as String: '(Px)'
    private Set<String> recommendations; // TODO: HashSet OK? instead of ArrayList. Iteration order important? HashSet has O(1) for contains() method
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
        } while (fired); // Need to re-check rules every time
    }


    class Rule {
        String[] conditions; // TODO: Is this change to array OK?
        String action;
        boolean activated; // Check if rule should be considered

        public Rule(String[] conditions, String action, boolean activated) {
            this.conditions = conditions;
            this.action = action;
            this.activated = activated;
        }

        // TODO: Complete hashCode and equals...


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Rule rule = (Rule) o;

            if (activated != rule.activated) return false;

            if (conditions != null) {
                if (rule.conditions == null)
                    return false;

                if (conditions.length != rule.conditions.length)
                    return false;

                for (int i = 0; i < conditions.length; i++) {
                    if (!conditions[i].equals(rule.conditions[i]))
                        return false;
                }
            } else {
                if (rule.conditions != null)
                    return false;
            }

            return action != null ? action.equals(rule.action) : rule.action == null;
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(conditions);
            result = 31 * result + (action != null ? action.hashCode() : 0);
            result = 31 * result + (activated ? 1 : 0);
            return result;
        }
    }

    private boolean isRecommendation(String action) {
        return action.charAt(1) == '#';
    }
}
