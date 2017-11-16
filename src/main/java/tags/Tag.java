package tags;

/**
 * Tag to be used throughout the Prometheus system. Possesses a confidence value.
 */
public abstract class Tag {
    private double confidence;

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

    double getConfidence() {
        return confidence;
    }

    void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public TagType getType() {
        return type;
    }
}
