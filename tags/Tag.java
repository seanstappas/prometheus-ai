package tags;

/**
 * Tag to be used throughout the Prometheus system.
 */
public class Tag {
    /**
     * The value of the Tag.
     */
    public String value;
    /**
     * The type of the Tag (FACT, RULE, or RECOMMENDATION).
     */
    public Type type;
    public enum Type {
        FACT,
        RULE,
        RECOMMENDATION
    }

    Tag() {}

    /**
     * Creates a Tag.
     * @param value the value of the Tag.
     * @param type the type of the Tag.
     */
    Tag(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Checks if the Tag is a Recommendation.
     * @return true if the Tag is a Recommendation.
     */
    public boolean isRecommendation() {
        return type == Type.RECOMMENDATION;
    }

    /**
     * Checks if the Tag is a Fact.
     * @return true if the Tag is a Fact.
     */
    public boolean isFact() {
        return type == Type.FACT;
    }

    /**
     * Checks if the Tag is a Rule.
     * @return true if the Tag is a Rule.
     */
    public boolean isRule() {
        return type == Type.RULE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return (value != null ? value.equals(tag.value) : tag.value == null) && type == tag.type;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value;
    }
}
