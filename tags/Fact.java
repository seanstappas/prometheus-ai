package tags;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Represents a fact in the Expert System. Facts are calculus predicates that represent something that is seen as
 * true.
 * <p>
 * Facts are composed of a predicate name and a set of arguments: P(ARG1, ARG2, ...)
 */

public class Fact extends Tag {

    private String predicateName;
    private LinkedList<Argument> arguments;

    public Fact() {
        this.type = TagType.FACT;
    }

    public Fact(String value) {

        this(value, 1.0);
    }

    public Fact(String value, double confidenceValue) {

        String[] tokens = value.split("[(),]");

        this.value = value;
        this.type = TagType.FACT;
        this.predicateName = tokens[0];
        this.arguments = argStringParser(tokens);
        this.confidenceValue = confidenceValue;


    }

    @Override
    public String toString() {
        return "[" + predicateName + '(' + arguments + ") " + confidenceValue*100 +"% ]";
    }

    public double getConfidenceValue() {
        return confidenceValue;
    }

    public void setConfidenceValue(double confidenceValue) {
        this.confidenceValue = confidenceValue;
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

    private LinkedList<Argument> argStringParser(String[] tokens) {
        LinkedList<Argument> argSet = new LinkedList<>();
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

    public LinkedList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(LinkedList<Argument> arguments) {
        this.arguments = arguments;
    }

    /**
     * Compares two facts to see if they are compatible
     * If matching occurs on a variable argument, return object includes a tuple (see VariableReturn class)
     *
     *
     * @param inputFact fact being compared
     * @return true if facts are 'matched' (notice not necessarily equal)
     */

    public VariableReturn matches(Fact inputFact) {

        VariableReturn result = new VariableReturn();
        if (!this.predicateName.equals(inputFact.predicateName)) {
            result.doesMatch = false;
            return result;
        }
        Iterator iterFact = this.arguments.iterator();
        Iterator iterInputFact = inputFact.arguments.iterator();

        if (this.arguments.size() >= inputFact.arguments.size()) {
            while (iterInputFact.hasNext()) {
                Argument argFact = (Argument) iterFact.next();
                Argument argInputFact = (Argument) iterInputFact.next();
                if (argFact.symbol.equals(Argument.argTypes.MATCHALL) || argInputFact.symbol.equals(Argument.argTypes.MATCHALL)) {
                    result.doesMatch = true;
                    return result;
                }
                if (argInputFact.symbol.equals(Argument.argTypes.VAR)) {
                    result.doesMatch = true;
                    result.pairs.put(argInputFact.getName(), argFact);
                }
                result.doesMatch = argFact.matches(argInputFact);
                if (!result.doesMatch) {
                    return result;
                }
            }
            if (iterFact.hasNext()) {
                Argument argFact = (Argument) iterFact.next();
                result.doesMatch = (argFact.symbol.equals(Argument.argTypes.MATCHALL));
                return result;
            }
            result.doesMatch = true;
            return result;

        } else {
            while (iterFact.hasNext()) {
                Argument argFact = (Argument) iterFact.next();
                Argument argInputFact = (Argument) iterInputFact.next();
                if (argFact.symbol.equals(Argument.argTypes.MATCHALL) || argInputFact.symbol.equals(Argument.argTypes.MATCHALL)) {
                    result.doesMatch = true;
                    return result;
                }
                if (argInputFact.symbol.equals(Argument.argTypes.VAR)) {
                    result.doesMatch = true;
                    result.pairs.put(argInputFact.getName(), argFact);
                }
                result.doesMatch = argFact.matches(argInputFact);
                if (!result.doesMatch) {
                    return result;
                }
            }
            if (iterInputFact.hasNext()) {
                Argument argFact = (Argument) iterInputFact.next();
                result.doesMatch = (argFact.symbol.equals(Argument.argTypes.MATCHALL));
                return result;
            }
            result.doesMatch = true;
            return result;
        }
    }

}
