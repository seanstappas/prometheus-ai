package tags;

import java.util.Arrays;

/**
 * Represents a rule in the expert system.
 */
public class Rule extends Tag{
    public Tag[] conditions; // TODO: Can a recommendation be a condition (or only facts..)?
    public Tag action;

    /**
     * Creates a Rule.
     * @param conditions condition tags of the rule
     * @param action action tag of the rule
     */
    public Rule(Tag[] conditions, Tag action) {
        this.conditions = conditions;
        this.action = action;
        this.type = Type.RULE;
        this.value = this.toString();
    }

    /**
     * Creates a Rule (for debugging purposes). Assumes all tags are facts.
     * @param args the conditions and action, in that order
     */
    public Rule(String... args) {
        Tag[] conditions = new Tag[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            conditions[i] = new Fact(args[i]);
        }
        this.conditions = conditions;
        this.action = new Fact(args[args.length - 1]);
        this.type = Type.RULE;
        this.value = this.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

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
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(conditions) + "=>" + action;
    }
}
