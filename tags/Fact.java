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

        this.value = value;
        this.type = TagType.FACT;
        this.predicateName = value.split("\\(")[0];
        this.arguments = argumentParser(value);

    }

    /**
     * Parses a raw string into a list of string tokens that represent each argument
     * <p>
     * Facts have zero, or more arguments [e.g. "P()", "P(ARG1)", "P(ARG1,ARG2,..., ARGN")]
     * NB: There should be no space characters between the arguments in a string
     *
     * @param str string input
     * @return list of string arguments
     */

    private LinkedList<Argument> argumentParser(String str) {
        String args = str.replaceAll("\\s+", "");
        try {
            args = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println("Fact string must contain parenthesis: " + e.getMessage());
        }
        String[] argTokens = args.split(",");
        LinkedList<Argument> argSet = new LinkedList<>();
        for (String argToken : argTokens) {
            if (argToken.length() > 0) {
                Argument argument = makeArgument(argToken);
                argSet.add(argument);
            }
        }
        return argSet;
    }

    /**
     * Calls the appropriate Argument constructor on a string token
     *
     * @param string String token
     * @return A single argument
     */

    private static Argument makeArgument(String string) {
        String[] tokens = string.split("[=><]");

        if (tokens.length > 1) {
            if (tokens[1].matches("-?\\d+(\\.\\d+)?")) {
                return new NumericArgument(string, tokens);
            } else if (tokens[0].equals("[?*]") || tokens[1].charAt(0) == '&') {
                return new VariableArgument(string, tokens);
            } else {
                return new StringArgument(string, tokens);
            }
        } else {
            if (tokens[0].matches("-?\\d+(\\.\\d+)?")) {
                return new NumericArgument(string, tokens);
            } else if (tokens[0].matches("[?*]") || tokens[0].charAt(0) == '&') {
                return new VariableArgument(string, tokens);
            } else {
                return new StringArgument(string, tokens);
            }
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
     *
     * @param inputFact fact being compared
     * @return true if facts are 'matched' (notice not necessarily equal)
     */

    //NEEDS TO RETURN LIST OF REPLACEMENTS // I.E. NOT A SINGLE ONE

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
