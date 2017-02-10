import java.util.ArrayList;
import java.util.HashMap;

/**
 * Expert System
 */
public class ES {
    private HashMap<String, Rule> rules;
    private ArrayList<Fact> facts;
    private ArrayList<String> recommendations; // TODO: Type of recommendations?

    public ES() {
        rules = new HashMap<>();
        facts = new ArrayList<>();
        recommendations = new ArrayList<>();
        reset(); // TODO: Why call reset?
    }

    public void reset() {
        rules.clear();
        facts.clear();
        recommendations.clear();
    }

    public void addFact(Fact fact) { // TODO: Fact is a String?
        facts.add(fact);
    }

    public void addRule(Rule rule) {
        rules.put(rule.condition, rule); // TODO: What is the rule key in the map?
    }

    private void addRecommendation(String rec) {
        recommendations.add(rec);
    }

    public ArrayList getRecommendations() {
        return recommendations;
    }

    public void think() { // TODO: Complete think() method

    }


    class Rule { // TODO: Right types?
        String condition;
        String action;
        boolean activated;

        public Rule(String condition, String action, boolean activated) {
            this.condition = condition;
            this.action = action;
            this.activated = activated;
        }
    }

    class Fact { // TODO: What is a predicate?
        /**
         * Single predicate calculus predicate
         *
         * @return
         */
        boolean predicate() {
            return false;
        }

        public Fact() {

        }
    }
}
