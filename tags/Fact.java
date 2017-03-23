package tags;

/**
 * Helper class to create a Fact object. A Fact represents a Predicate.
 */
public class Fact extends Tag {
    /**
     * Creates a Fact.
     * @param value the value of the Fact.
     */
    public Fact(String value) {
        super(value, Type.FACT);
    }
}
