package tags;

/**
 * Represents a fact in the Expert System. Facts are simple calculus predicates that represent something that is seen as
 * true.
 */
public class Fact extends Tag {
    /**
     * Creates a Fact.
     *
     * @param value  the value of the Fact
     */
    public Fact(String value) {
        super(value, TagType.FACT);
    }
}
