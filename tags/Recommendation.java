package tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recommendation in the Expert System. Recommendations are for specific actions to be taken (walk, stop,
 * etc.).
 *<p>
 * Recommendations are composed of a predicate name and a set of arguments: @P(ARG1, ARG2, ...)
 */
public class Recommendation extends Tag implements IPredicate {
    /**
     * Creates a Recommendation.
     *
     * @param value the value of the Recommendation
     */

    private String predicateName;
    private List<Argument> arguments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Recommendation rec = (Recommendation) o;

        if (predicateName != null ? !predicateName.equals(rec.predicateName) : rec.predicateName != null)
            return false;
        return arguments != null ? arguments.equals(rec.arguments) : rec.arguments == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (predicateName != null ? predicateName.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }

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
     * Prints predicate name, arguments and confidence value of recommendation
     * <p>
     * e.g. "[@P(ARG1, ARG2) 100%]"
     * @return string value of Recommendation
     */

    @Override
    public String toString() {
        return "[@" + predicateName + '(' + arguments + ") " + getConfidenceValue() * 100 + "% ]";
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

}

