package tags;

/**
 * Tag to be used throughout the Prometheus system. Possesses a confidence
 * value.
 */
public abstract class Tag {
    double confidence;

    public double getConfidence() {
        return confidence;
    }

    abstract String simpleToString();
}
