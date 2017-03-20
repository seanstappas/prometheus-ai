package tags;

import java.util.Arrays;

public class Rule {
    public String[] conditions;
    public Tag action;

    /**
     * Full constructor
     * @param conditions
     * @param action
     */
    public Rule(String[] conditions, Tag action) {
        this.conditions = conditions;
        this.action = action;
    }

    /**
     * Easy constructor
     * @param args The conditions and action, in that order
     */
    public Rule(String... args) {
        String[] conditions = new String[args.length - 1];
        System.arraycopy(args, 0, conditions, 0, args.length - 1);
        this.conditions = conditions;
        this.action = new Tag(args[args.length - 1], false);
    }

    /**
     * Necessary to properly implement a Set of Rules
     * @param o
     * @return
     */
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

    /**
     * Simple String representation. For debugging purposes.
     * @return
     */
    @Override
    public String toString() {
        return Arrays.toString(conditions) + "=>" + action;
    }
}
