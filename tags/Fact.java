package tags;

import java.util.LinkedHashSet;
import java.util.Iterator;

/**
 * Represents a fact in the Expert System. Facts are calculus predicates that represent something that is seen as
 * true.
 *
 * Facts are composed of a predicate name and a set of arguments: P(ARG1, ARG2, ...)
 */

public class Fact extends Tag {

    private String predicateName;
    private LinkedHashSet<Argument> arguments;

    public Fact() {
        this.type = TagType.FACT;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public LinkedHashSet<Argument> getArguments() {
        return arguments;
    }

    public Fact(String value) {

        this.value = value;
        this.type = TagType.FACT;
        this.predicateName = value.split("\\(")[0];
        this.arguments = argumentParser(value);

    }

    /**
     * Parses a raw string into a list of string tokens that represent each argument
     *
     * @param str string input
     * @return list of string arguments
     */

    private LinkedHashSet<Argument> argumentParser(String str) {
        String args = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        String[] argTokens = args.split(",");
        LinkedHashSet<Argument> argSet = new LinkedHashSet<>();
        for (String argToken: argTokens) {
                Argument argument = makeArgument(argToken);
                argSet.add(argument);
        }
        return argSet;
    }

    /**
     * Calls the appropriate Argument constructor on a string token
     * @param string String token
     * @return A single argument
     */

    private static Argument makeArgument(String string) {
        String[] tokens = string.split("[=><]");

        if (tokens[1].matches("-?\\d+(\\.\\d+)?")) { //cleanup
            return new NumericArgument(string, tokens);
        } else if (tokens[0].equals("[?*]") || tokens[1].charAt(0) == '&' ) {
            return new VariableArgument(string, tokens);
        } else {
            return new StringArgument(string, tokens);
        }
    }

    /**
     * Compares two facts to see if they are compatible
     * @param that fact being compared
     * @return true if facts are 'matched' (notice not necessarily equal)
     */

    public Boolean matches(Fact that) {

        if (!this.predicateName.equals(that.predicateName)) {
            return false;
        }
        if (this.arguments.containsAll(that.arguments)) {
            return true;
        }
        if (this.arguments.size() != that.arguments.size()
                && (!this.arguments.contains(new Argument("*")) || !this.arguments.contains(new Argument("*")))) {
            return false;
        }
        if (this.arguments.size() >= that.arguments.size()) {
            return this.matchesHelper(that);
        }
        else {
            return that.matchesHelper(this);
        }
    }

    private Boolean matchesHelper(Fact that) {
        Iterator iterOne = this.arguments.iterator();
        Iterator iterTwo = that.arguments.iterator();
        while (iterOne.hasNext()) {
            Argument arg1 = (Argument) iterOne.next(); Argument arg2 = (Argument) iterTwo.next();
                if (arg1.symbol.equals(Argument.argTypes.MATCHALL) || arg2.symbol.equals(Argument.argTypes.MATCHALL)) {
                    return true;
                }
                if (!arg1.matches(arg2)) {
                    return false;
                }
            }
        return true;
    }
}
