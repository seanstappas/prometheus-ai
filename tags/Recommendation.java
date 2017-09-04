package tags;

/**
 * Represents a recommendation in the Expert System. Recommendations are for specific actions to be taken (walk, stop,
 * etc.).
 */
public class Recommendation extends Fact {
    /**
     * Creates a Recommendation.
     *
     * @param value the value of the Recommendation
     */
    public Recommendation(String value) {
        super(value);
        this.type = TagType.RECOMMENDATION;
        this.confidenceValue = 1.0;
    }

    public Recommendation(String value, double confidenceValue) {
        super(value);
        this.type = TagType.RECOMMENDATION;
        this.confidenceValue = confidenceValue;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
