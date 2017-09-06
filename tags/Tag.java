package tags;

/**
 * Tag to be used throughout the Prometheus system. Possesses a confidence value.
 */
public abstract class Tag {
    private double confidenceValue;

    /**
     * The type of the Tag (FACT, RULE, or RECOMMENDATION).
     */
    public TagType type;

    public enum TagType {
        FACT,
        RULE,
        RECOMMENDATION
    }

    double getConfidenceValue() {
        return confidenceValue;
    }

    public void setConfidenceValue(double confidenceValue) {
        this.confidenceValue = confidenceValue;
    }

    public TagType getType() {
        return type;
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
