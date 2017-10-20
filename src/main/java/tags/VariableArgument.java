package tags;

/**
 * Subclass for arguments that have variable values
 * <p>
 * i.e. "?", "*" or {@literal "&x"}
 */

final class VariableArgument extends Argument {

    /**
     * Constructor of variable Arguments
     * <p>
     * Arguments must be a string made up of alpha characters, as well as one of ["*", "?", {@literal &}] characters
     * @param string argument as a string
     * @param tokens argument as tokens, split on mathematical symbols
     */

    VariableArgument(String string, String[] tokens) {

        super(tokens);

        if (tokens[0].equals("*")) {
            this.setSymbol(ArgTypes.MATCHALL);
            this.setName("*");
        } else if (tokens[0].equals("?")) {
            this.setSymbol(ArgTypes.MATCHONE);
            this.setName("?");
        } else if (tokens[0].charAt(0) == '&') {
            this.setSymbol(ArgTypes.VAR);
            this.setName(tokens[0]);
        }
    }

    /**
     * Prints name (when appropriate), and type
     * @return Variable argument as string.
     */

    @Override
    public String toString() {
        switch (getSymbol()) {
            case MATCHONE:
                return "?";
            case MATCHALL:
                return "*";
            case VAR:
                return getName() + "";
            default:
                return super.toString();
        }
    }
}
