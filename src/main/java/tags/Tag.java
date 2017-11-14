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

    public enum TagType { // TODO: remove this tag type, just use instanceof
        FACT,
        RULE,
        RECOMMENDATION,
        EMPTY
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
}
