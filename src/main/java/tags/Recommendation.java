package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recommendation in the Expert System. Recommendations are for specific actions to be taken (walk, stop,
 * etc.).
 *<p>
 * Recommendations are composed of a predicate name and a set of arguments: @P(ARG1, ARG2, ...)
 */
public class Recommendation extends Tag implements Predicate {
    private String predicateName;
    private List<Argument> arguments;

    /**
     * Constructs a Recommendation object from a string
     * <p>
     * NB: There should be no space characters between the arguments in a string i.e. "@P(ARG1,ARG2,ARG3...)"
     * Recommendation strings begin with "@" character.
     * Arguments are delimited by commas within parenthesis.
     *
     * @param value           String input
     * @param confidenceValue double in range [0,1] i.e. 0.n representing n0% confidence
     */

    public Recommendation(String value, double confidenceValue) {

        String[] tokens = value.split("[(),]");

        this.type = TagType.RECOMMENDATION;
        this.predicateName = tokens[0].replace("@", "");
        this.arguments = argStringParser(tokens);
        this.setConfidenceValue(confidenceValue);
    }

    /**
     * {@code confidenceValue} defaults to 1.0
     *
     * @see #Recommendation(String, double)
     */
    public Recommendation(String value) {
        this(value, 1.0);
    }

    /**
     * Parses a raw string into a list of string tokens that represent each argument
     * @param tokens string input
     * @return list of string arguments
     */

    private List<Argument> argStringParser(String[] tokens) {
        List<Argument> argSet = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            Argument argument = makeArgument(tokens[i]);
            argSet.add(argument);
        }
        return argSet;
    }

    /**
     * Calls the appropriate Argument constructor on a string token
     * <p>
     * @see Fact#makeArgument(String)
     *
     * @param argString String token
     * @return A single argument
     */

    private static Argument makeArgument(String argString) {
        return Fact.makeArgument(argString);
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<Argument> getArguments() {
        return arguments;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("predicateName", predicateName)
                .append("arguments", arguments)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Recommendation that = (Recommendation) o;

        return new EqualsBuilder()
                .append(predicateName, that.predicateName)
                .append(arguments, that.arguments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(predicateName)
                .append(arguments)
                .toHashCode();
    }
}

