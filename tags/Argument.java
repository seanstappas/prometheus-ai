package tags;

/**
 * Arguments are composed of a name and symbol
 */

public class Argument {

    String name;
    ArgTypes symbol;

    public enum ArgTypes {
        /**
         * argument is made up of a string (e.g. tall)
         */
        STRING,
        /**
         * argument is a name equal to an integer (e.g. height = 10)
         */
        EQ,
        /**
         * argument is a name greater than an integer (e.g. height &gt; 10)
         */
        GT,
        /**
         * argument is a name less that an integer (e.g. height &lt; 10)
         */
        LT,
        /**
         * argument matches on a corresponding argument in a fact with same predicate name (see BASH '?')
         */
        MATCHONE,
        /**
         * argument is a variable integer (e.g. height = {@literal &x})
         */
        VAR,
        /**
         * argument matches on &gt; 0 arguments in a fact with same predicate name (see BASH '*')
         */
        MATCHALL,
        /**
         * argument is made up of an integer value (e.g. 10)
         */
        INT
    }

    public String getName() {
        return name;
    }

    public ArgTypes getSymbol() {
        return symbol;
    }

    /**
     * Constructor for Argument
     * <p>
     * Attempts to split an argument string (e.g. height=10) on the mathematical symbol (in this case 10),
     * assigns name to first token, if there are multiple tokens
     * @param tokens argument string split on mathematical symbols
     */

    Argument(String[] tokens) {
        if (tokens.length > 1) {
            this.name = tokens[0];
        } else {
            this.name = "";
        }
    }

    /**
     * Compares two arguments, calling appropriate overloaded method
     *
     * @param inputFact argument from rule-set
     * @return true if two arguments match
     */

    boolean matches(Argument inputFact) {
        switch (this.symbol) {
            case STRING:
                if (inputFact.symbol.equals(ArgTypes.VAR)) {
                    return true;
                }
                if (inputFact.symbol.equals(ArgTypes.STRING) || inputFact.symbol.equals(ArgTypes.VAR)) {
                    return ((StringArgument) this).matches((StringArgument) inputFact);
                }
                return (inputFact.symbol.equals(ArgTypes.MATCHONE));
            case EQ:
            case GT:
            case LT:
            case INT:
                if (inputFact.symbol.equals(ArgTypes.VAR)) {
                    return true;
                }
                if (inputFact.symbol.equals(ArgTypes.INT) ||
                        inputFact.symbol.equals(ArgTypes.EQ) ||
                        inputFact.symbol.equals(ArgTypes.LT) ||
                        inputFact.symbol.equals(ArgTypes.GT)) {
                    return ((NumericArgument) this).matches((NumericArgument) inputFact);
                }
                return (inputFact.symbol.equals(ArgTypes.MATCHONE));
            case MATCHONE:
                return !inputFact.symbol.equals(ArgTypes.VAR);
            case VAR:
                return false;
            default:
                return false;
        }
    }


    @Override
    public String toString() {
        return "" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Argument argument = (Argument) o;

        if (!name.equals(argument.name)) return false;
        return symbol == argument.symbol;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + symbol.hashCode();
        return result;
    }
}

/**
 * Subclass for arguments that have integer values
 * <p>
 * If argument has a negative value, isNeg == true.
 *  i.e. "ARG != 5" {@literal ->} this.value==5; this.isNeg==true
 */

class NumericArgument extends Argument {

    private boolean isNeg;
    private int value;

    private boolean isNeg() {
        return !isNeg;
    }

    /**
     * Compares two numeric arguments to see if they match
     *
     * @param that numericArgument to compare with this
     * @return true if matching
     */

    boolean matches(NumericArgument that) {
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.isNeg && that.isNeg) {
            return false;
        }
        if (this.isNeg || that.isNeg) {
            return (this.value != that.value);
        }

        switch (this.symbol) {
            case EQ:
                switch (that.symbol) {
                    case EQ:
                        return this.value == that.value;
                    case GT:
                        return this.value > that.value;
                    case LT:
                        return this.value < that.value;
                }
            case GT:
                switch (that.symbol) {
                    case EQ:
                        return that.value > this.value;
                    case GT:
                        return false;
                    case LT:
                        return false;
                }
            case LT:
                switch (that.symbol) {
                    case EQ:
                        return that.value < this.value;
                    case GT:
                        return false;
                    case LT:
                        return false;
                }
            default:
                return true;
        }
    }

    /**
     * Constructor of numeric arguments
     * <p>
     * Arguments must be a string that is purely numeric e.g. "5
     *  or composed of a name delimited by {@literal ["<",">,"="]}
     *
     * @param string argument as a string
     * @param tokens argument as tokens, split on mathematic symbols
     */

    NumericArgument(String string, String[] tokens) {

        super(tokens);
        this.isNeg = (string.contains("!"));

        if (string.contains("=")) {
            this.symbol = ArgTypes.EQ;
        } else if (string.contains(">")) {
            this.symbol = ArgTypes.GT;
        } else if (string.contains("<")) {
            this.symbol = ArgTypes.LT;
        } else {
            this.symbol = ArgTypes.INT;
        }

        this.value = Integer.parseInt(tokens[tokens.length - 1]);

    }

    /**
     * Prints name (when appropriate), symbol and value
     * @return the Argument as a String.
     */

    @Override
    public String toString() {
        switch (symbol) {
            case INT:
                if (!isNeg()) {
                    return "" + value;
                } else {
                    return "!=" + value;
                }
            case EQ:
                if (!isNeg()) {
                    return name + " = " + value;
                } else return name + " !=" + value;
            case LT:
                if (!isNeg()) {
                    return name + " < " + value;
                } else return name + " !<" + value;
            case GT:
                if (!isNeg()) {
                    return name + " > " + value;
                } else return name + " !>" + value;
            default:
                return super.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NumericArgument that = (NumericArgument) o;

        if (isNeg != that.isNeg) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isNeg ? 1 : 0);
        result = 31 * result + value;
        return result;
    }
}

/**
 * Subclass for arguments that have string values
 * <p>
 *  If argument has a negative value, isNeg == true.
 *  i.e. "ARG != big" {@literal ->} this.value.equals("big"; this.isNeg==true
 */

class StringArgument extends Argument {

    private boolean isNeg;
    private String value;

    private boolean isNeg() {
        return !isNeg;
    }

    /**
     * Compares two string arguments to see if they match
     * @param that stringArgument to compare with this
     * @return true if matching
     */

    boolean matches(StringArgument that) {
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.isNeg && that.isNeg) {
            return false;
        }
        if (this.isNeg || that.isNeg) {
            return (!this.value.equals(that.value));
        } else {
            return this.value.equals(that.value);
        }
    }

    /**
     * Constructor of string Arguments
     * <p>
     * Arguments must be a string made up of alpha characters, can contain ["=", "!"] characters
     * @param string argument as a string
     * @param tokens argument as tokens, split on mathematical symbols
     */

    StringArgument(String string, String[] tokens) {

        super(tokens);

        isNeg = (string.contains("!"));
        value = tokens[tokens.length - 1];
        symbol = ArgTypes.STRING;
    }

    /**
     * Prints name (when appropriate), symbol and value
     * @return the Argument as a String.
     */

    @Override
    public String toString() {
        if (this.name.equals("")) {
            if (!isNeg()) {
                return "" + value;
            } else {
                return "!=" + value;
            }
        } else {
            if (!isNeg()) {
                return name + " = " + value;
            } else {
                return name + " != " + value;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StringArgument that = (StringArgument) o;

        if (isNeg != that.isNeg) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isNeg ? 1 : 0);
        result = 31 * result + value.hashCode();
        return result;
    }
}

/**
 * Subclass for arguments that have variable values
 * <p>
 * i.e. "?", "*" or {@literal "&x"}
 */

class VariableArgument extends Argument {

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
            this.symbol = ArgTypes.MATCHALL;
            this.name = "*";
        } else if (tokens[0].equals("?")) {
            this.symbol = ArgTypes.MATCHONE;
            this.name = "?";
        } else if (tokens[0].charAt(0) == '&') {
            this.symbol = ArgTypes.VAR;
            this.name = tokens[0];
        }
    }

    /**
     * Prints name (when appropriate), and type
     * @return Variable argument as string.
     */

    @Override
    public String toString() {
        switch (symbol) {
            case MATCHONE:
                return "?";
            case MATCHALL:
                return "*";
            case VAR:
                return name + "";
            default:
                return super.toString();
        }
    }
}

