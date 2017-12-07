package tags;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a recommendation in the Expert System. Recommendations are for
 * specific actions to be taken (walk, stop, etc.).
 * <p>
 * Recommendations are composed of a predicate name and a set of arguments:
 * <p>
 * "@P(ARG1, ARG2, ...)"
 */
public final class Recommendation extends Predicate {
    /**
     * Constructs a Recommendation object from a string
     * <p>
     * NB: There should be no space characters between the arguments in a string
     * i.e. "@P(ARG1,ARG2,ARG3...)" Recommendation strings begin with "@"
     * character. Arguments are delimited by commas within parenthesis.
     *
     * @param value      String input
     * @param confidence double in range [0,1] i.e. 0.n representing n0%
     *                   confidence
     */

    public Recommendation(final String value, final double confidence) {
        final String[] tokens = value.split("[(),]");

        this.setPredicateName(tokens[0].replace("@", ""));
        this.setArguments(argStringParser(tokens));
        this.setConfidence(confidence);
    }

    /**
     * {@code confidenceValue} defaults to 1.0.
     *
     * @param value the Recommendation String value
     * @see #Recommendation(String, double)
     */
    public Recommendation(final String value) {
        this(value, 1.0);
    }

    Recommendation(final String predicateName, final List<Argument> arguments,
                   final double confidence) {
        this.setPredicateName(predicateName);
        this.setArguments(arguments);
        this.setConfidence(confidence);
    }

    /**
     * Calls the appropriate Argument constructor on a string token.
     * <p>
     *
     * @param argString String token
     * @return A single argument
     * @see Fact#makeArgument(String)
     */

    private static Argument makeArgument(final String argString) {
        return Fact.makeArgument(argString);
    }

    @Override
    Predicate getPredicateCopy() {
        return new Recommendation(getPredicateName(), getArguments(),
                getConfidence());
    }

    /**
     * Parses a raw string into a list of string tokens that represent each
     * argument.
     *
     * @param tokens string input
     * @return list of string arguments
     */
    private List<Argument> argStringParser(final String[] tokens) {
        final List<Argument> argSet = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            final Argument argument = makeArgument(tokens[i]);
            argSet.add(argument);
        }
        return argSet;
    }

    /**
     * Prints predicate name, arguments and confidence value of recommendation
     * <p>
     * e.g. "[@P(ARG1, ARG2) 100%]"
     *
     * @return string value of Recommendation
     */
    @Override
    public String toString() {
        return simpleToString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Recommendation that = (Recommendation) o;

        return new EqualsBuilder()
                .append(getPredicateName(), that.getPredicateName())
                .append(getArguments(), that.getArguments())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getPredicateName())
                .append(getArguments())
                .toHashCode();
    }

    @Override
    String simpleToString() {
        return MessageFormat.format(
                "@{0}{1}",
                getPredicateName(),
                getArguments());
    }
}

