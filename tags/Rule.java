package tags;

import java.util.Arrays;

public class Rule extends Tag{
    public Tag[] conditions;
    public Tag action;

    /**
     * Full constructor
     * @param conditions
     * @param action
     */
    public Rule(Tag[] conditions, Tag action) {
        this.conditions = conditions;
        this.action = action;
        this.type = TagType.RULE;
        this.value = this.toString();
    }

    /**
     * Easy constructor (for debugging purposes). Assumes all tags are facts.
     * @param args The conditions and action, in that order
     */
    public Rule(String... args) {
        Tag[] conditions = new Tag[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            conditions[i] = new Tag(args[i], TagType.FACT);
        }
        this.conditions = conditions;
        this.action = new Tag(args[args.length - 1], TagType.FACT);
        this.type = TagType.RULE;
        this.value = this.toString();
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
