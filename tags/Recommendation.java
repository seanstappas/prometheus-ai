package tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recommendation in the Expert System. Recommendations are for specific actions to be taken (walk, stop,
 * etc.).
 */
public class Recommendation extends Tag implements IPredicate {
    /**
     * Creates a Recommendation.
     *
     * @param value the value of the Recommendation
     */

    private String predicateName;
    private List<Argument> arguments;

    public Recommendation() {
        this.type = TagType.RECOMMENDATION;
    }

    public Recommendation(String value) {

        this(value, 1.0);
    }

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

    public Recommendation(String value, double confidenceValue) {

        String[] tokens = value.split("[(),]");

        this.type = TagType.RECOMMENDATION;
        this.predicateName = tokens[0].replace("@", "");
        this.arguments = argStringParser(tokens);
        this.confidenceValue = confidenceValue;
    }

    @Override
    public String toString() {
        return "[@" + predicateName + '(' + arguments + ") " + confidenceValue * 100 + "% ]";
    }

    /**
     * Parses a raw string into a list of string tokens that represent each argument
     * <p>
     * Facts have zero, or more arguments [e.g. "P()", "P(ARG1)", "P(ARG1,ARG2,..., ARGN")]
     * NB: There should be no space characters between the arguments in a string
     *
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
     *
     * @param argString String token
     * @return A single argument
     */

    private static Argument makeArgument(String argString) {
        String[] argTokens = argString.split("[=><!]");
        int lastElem = argTokens.length - 1;

        if (argTokens[lastElem].matches("-?\\d+(\\.\\d+)?")) {
            return new NumericArgument(argString, argTokens);
        } else if (argTokens[lastElem].matches("[?*]") || argTokens[lastElem].charAt(0) == '&') {
            return new VariableArgument(argString, argTokens);
        } else {
            return new StringArgument(argString, argTokens);
        }
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

}

