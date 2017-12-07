package tags;

/**
 * Tag to be used throughout the Prometheus system. Possesses a confidence
 * value.
 */
public abstract class Tag {
    private double confidence;

    /**
     * @return the confidence of the Tag
     */
    public final double getConfidence() {
        return confidence;
    }

    /**
     * Sets the confidence of the Tag.
     *
     * @param confidence the confidence of the Tag
     */
    final void setConfidence(final double confidence) {
        this.confidence = confidence;
    }

    /**
     * @return a simple String representation of the Tag
     */
    abstract String simpleToString();
}
