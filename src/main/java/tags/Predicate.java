package tags;

import java.util.List;

/**
 * Interface for Predicates (Implemented by Fact and Recommendation)
 */

public abstract class Predicate extends Tag {
    public abstract String getPredicateName();
    public abstract List<Argument> getArguments();
}
