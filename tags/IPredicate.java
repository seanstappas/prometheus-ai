package tags;

import java.util.List;

/**
 * Interface for Predicates (Implemented by Fact and Recommendation)
 */

public interface IPredicate {
    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    String getPredicateName();

    void setConfidenceValue(double confidenceValue);

    List<Argument> getArguments();

    Tag.TagType getType();

}
