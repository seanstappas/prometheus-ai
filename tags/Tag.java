package tags;

/**
 * Tag to be used throughout the Prometheus system.
 */
public abstract class Tag {
    /**
     * The value of the Tag.
     */
    public String value;
    double confidenceValue;

    /**
     * The type of the Tag (FACT, RULE, or RECOMMENDATION).
     */
    public TagType type;

    public enum TagType {
        FACT,
        RULE,
        RECOMMENDATION
    }

    Tag() {
    }

    /**
     * Creates a Tag.
     *
     * @param value the value of the Tag
     * @param type  the Type of the Tag
     */
    protected Tag(String value, TagType type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Creates a Tag from a String.
     *
     * @param value the String value of the Tag
     * @param type  the Type of the Tag
     * @return the created Tag
     */
    public static Tag createTagFromString(String value, TagType type) {
        switch (type) {
            case RECOMMENDATION:
                return new Recommendation(value);
            case FACT:
                return new Fact(value);
            case RULE:
                return Rule.makeRules(value).get(0); //check
        }
        return null;
    }

    /**
     * Checks if the Tag is a Recommendation.
     *
     * @return <code>true</code> if the Tag is a Recommendation.
     */
    public boolean isRecommendation() {
        return type == TagType.RECOMMENDATION;
    }

    /**
     * Checks if the Tag is a Fact.
     *
     * @return <code>true</code> if the Tag is a Fact
     */
    public boolean isFact() {
        return type == TagType.FACT;
    }

    /**
     * Checks if the Tag is a Rule.
     *
     * @return <code>true</code> if the Tag is a Rule
     */
    public boolean isRule() {
        return type == TagType.RULE;
    }

    public void setConfidenceValue(double confidenceValue) {
        this.confidenceValue = confidenceValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (Double.compare(tag.confidenceValue, confidenceValue) != 0) return false;
        return type == tag.type;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(confidenceValue);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return value;
    }
}
