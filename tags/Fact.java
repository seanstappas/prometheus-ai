package tags;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a fact in the Expert System. Facts are simple calculus predicates that represent something that is seen as
 * true.
 */

public class Fact extends Tag {

    private String predicate;
    private ArrayList<Argument> arguments;
    private boolean multiMatch;

    public Fact() {
        this.type = TagType.FACT;
    }

    public Fact(String value) {

        this.value = value;
        this.type = TagType.FACT;
        this.predicate = value.split("\\(")[0];
        this.multiMatch = false;
        this.arguments = argumentParser(value);

    }

    /**
     * Parses a raw string into a list of string tokens that represent each argument
     * @param str string input
     * @return list of string arguments
     */

    public ArrayList<Argument> argumentParser(String str) {
        String args = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        String[] argTokens = args.split(",");
        ArrayList<Argument> argList = new ArrayList<>();
        for (int i = 0; i < argTokens.length; i++) {
            if (argTokens[i].equals("*")) {
                this.multiMatch = true;
            }
            else {
                Argument argument = makeArgument(argTokens[i]);
                argList.add(argument);
            }
        }
        return argList;
    }

    /**
     * Calls the appropriate Argument constructor on a string token
     * @param string String token
     * @return A single argument
     */

    public static Argument makeArgument(String string) {
        String[] tokens = string.split("[=><]");

        if (tokens[1].matches("-?\\d+(\\.\\d+)?")) { //cleanup
            return new numericArgument(string, tokens);
        } else if (tokens[0].equals("?")) {
            return new Argument(string);
        } else if(tokens[1].charAt(0) == '&') {
            return new Argument(string);
        } else {
            return new stringArgument(string, tokens);
        }
    }

    public String getPredicate() {
        return predicate;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    /**
     * Compares two facts to see if they are compatible
     * @param that fact being compared
     * @return true if facts are 'matched' (notice not necessarily equal)
     */

    //TODO: Complete matches
    public Boolean factMatches(Fact that) {
        if (!this.predicate.equals(that.predicate)) {
            return false;
        }
        else if (this.arguments.size() != that.arguments.size()) {
        if (!this.multiMatch || !that.multiMatch) {
                return false;
            }
        }
        if (this.arguments.equals(that.arguments)) {
            return true;
        }
        else {
            for (Argument a1 : this.arguments) {
                for (Argument a2 : that.arguments) {
                    if (a1.matches(a2)) {

                    }
                }
            }
        }

        return true;
    }


}


