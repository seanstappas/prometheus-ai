package tags;

/**
 * Tag to be used throughout the Prometheus system.
 */
public abstract class Tag {
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

    public TagType getType() {
        return type;
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
}
