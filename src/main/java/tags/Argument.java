package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Arguments are composed of a name and symbol
 */

public class Argument {

    private String name;
    private ArgTypes symbol;

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(ArgTypes symbol) {
        this.symbol = symbol;
    }

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
         * argument getMatchResult on a corresponding argument in a fact with same predicate name (see BASH '?')
         */
        MATCHONE,
        /**
         * argument is a variable integer (e.g. height = {@literal &x})
         */
        VAR,
        /**
         * argument getMatchResult on &gt; 0 arguments in a fact with same predicate name (see BASH '*')
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
        return new ToStringBuilder(this)
                .append("name", name)
                .append("symbol", symbol)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Argument argument = (Argument) o;

        return new EqualsBuilder()
                .append(name, argument.name)
                .append(symbol, argument.symbol)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(symbol)
                .toHashCode();
    }
}

