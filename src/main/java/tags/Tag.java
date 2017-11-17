package tags;

/**
 * Tag to be used throughout the Prometheus system. Possesses a confidence value.
 */
public abstract class Tag {
    private double confidence;

    double getConfidence() {
        return confidence;
    }

    void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
