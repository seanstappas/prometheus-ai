package tags;

/**
 * Arguments are composed of a name and symbol.
 */
public abstract class Argument {
    private String name;
    private ArgType symbol;

    /**
     * Constructor for Argument
     * <p>
     * Attempts to split an argument string (e.g. height=10) on the mathematical
     * symbol (in this case 10), assigns name to first token, if there are
     * multiple tokens
     *
     * @param tokens argument string split on mathematical symbols
     */
    Argument(final String[] tokens) {
        if (tokens.length > 1) {
            this.name = tokens[0];
        } else {
            this.name = "";
        }
    }

    /**
     * @return the name of the argument
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the argument.
     *
     * @param name the name
     */
    final void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the symbol of the argument
     */
    public final ArgType getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol of the argument.
     *
     * @param symbol the symbol
     */
    final void setSymbol(final ArgType symbol) {
        this.symbol = symbol;
    }

    /**
     * Compares two arguments, calling appropriate overloaded method.
     *
     * @param inputFact argument from rule-set
     * @return true if two arguments match
     */
    final boolean matches(final Argument inputFact) {
        switch (this.symbol) {
            case STRING:
                if (inputFact.symbol.equals(ArgType.VAR)) {
                    return true;
                }
                if (inputFact.symbol.equals(ArgType.STRING)
                        || inputFact.symbol.equals(ArgType.VAR)) {
                    return ((StringArgument) this)
                            .matches((StringArgument) inputFact);
                }
                return (inputFact.symbol.equals(ArgType.MATCHONE));
            case EQ:
            case GT:
            case LT:
            case INT:
                if (inputFact.symbol.equals(ArgType.VAR)) {
                    return true;
                }
                if (inputFact.symbol.equals(ArgType.INT)
                        || inputFact.symbol.equals(ArgType.EQ)
                        || inputFact.symbol.equals(ArgType.LT)
                        || inputFact.symbol.equals(ArgType.GT)) {
                    return ((NumericArgument) this)
                            .matches((NumericArgument) inputFact);
                }
                return (inputFact.symbol.equals(ArgType.MATCHONE));
            case MATCHONE:
                return !inputFact.symbol.equals(ArgType.VAR);
            case VAR:
                return false;
            default:
                return false;
        }
    }

    /**
     * Argument type.
     */
    public enum ArgType {
        /**
         * Argument is made up of a string (e.g. tall).
         */
        STRING,
        /**
         * Argument is a name equal to an integer (e.g. height = 10).
         */
        EQ,
        /**
         * Argument is a name greater than an integer (e.g. height &gt; 10).
         */
        GT,
        /**
         * Argument is a name less that an integer (e.g. height &lt; 10).
         */
        LT,
        /**
         * Argument getMatchResult on a corresponding argument in a fact with
         * same predicate name (see BASH '?').
         */
        MATCHONE,
        /**
         * Argument is a variable integer (e.g. height = {@literal &x}).
         */
        VAR,
        /**
         * Argument getMatchResult on &gt; 0 arguments in a fact with same
         * predicate name (see BASH '*').
         */
        MATCHALL,
        /**
         * Argument is made up of an integer value (e.g. 10).
         */
        INT
    }
}
