package tags;

import java.util.List;

public interface IPredicate {
    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    double getConfidenceValue();

    void setConfidenceValue(double confidenceValue);

    String getPredicateName();

    List<Argument> getArguments();

    Tag.TagType getType();

    void setArguments(List<Argument> arguments);

    VariableReturn matches(Fact inputFact);
}
