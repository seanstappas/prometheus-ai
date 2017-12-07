package tags;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Interface for Predicates (Implemented by Fact and Recommendation).
 */
public abstract class Predicate extends Tag {
    private String predicateName;
    private List<Argument> arguments;

    /**
     * @return a copy of the current Predicate
     */
    abstract Predicate getPredicateCopy();

    /**
     * @return the predicate name
     */
    public final String getPredicateName() {
        return predicateName;
    }

    /**
     * Sets the predicate name.
     *
     * @param predicateName the predicate name
     */
    final void setPredicateName(final String predicateName) {
        this.predicateName = predicateName;
    }

    /**
     * @return the arguments
     */
    public final List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    /**
     * Sets the arguments.
     *
     * @param arguments the arguments
     */
    final void setArguments(final List<Argument> arguments) {
        this.arguments = arguments;
    }

    /**
     * Returns a copy of the current Predicate with replaced variable
     * argument(s) with a String or Numeric Argument.
     *
     * @param pendingReplacementPairs the pending replacement pairs
     * @return a copy of the current Predicate with replaced variable
     * argument(s) with a String or Numeric Argument.
     */
    public final Predicate replaceVariableArguments(
            final Map<String, Argument> pendingReplacementPairs) {
        final Predicate p = getPredicateCopy();
        int argumentIndex = 0;
        for (final Argument argument : p.arguments) {
            if (pendingReplacementPairs.containsKey(argument.getName())) {
                arguments.set(argumentIndex,
                        pendingReplacementPairs.get(argument.getName()));
            }
            argumentIndex++;
        }
        return p;
    }
}
