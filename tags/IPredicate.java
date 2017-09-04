package tags;

import java.util.List;

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
