package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Subclass for arguments that have integer values
 * <p>
 * If argument has a negative value, isNeg == true.
 * i.e. "ARG != 5" {@literal ->} this.value==5; this.isNeg==true
 */
final class NumericArgument extends Argument {
    private final boolean isNeg;
    private final int value;

    /**
     * Constructor of numeric arguments.
     * <p>
     * Arguments must be a string that is purely numeric e.g. "5
     * or composed of a name delimited by {@literal ["<",">,"="]}
     *
     * @param string argument as a string
     * @param tokens argument as tokens, split on mathematiccal symbols
     */
    NumericArgument(final String string, final String[] tokens) {
        super(tokens);
        this.isNeg = (string.contains("!"));

        if (string.contains("=")) {
            this.setSymbol(ArgType.EQ);
        } else if (string.contains(">")) {
            this.setSymbol(ArgType.GT);
        } else if (string.contains("<")) {
            this.setSymbol(ArgType.LT);
        } else {
            this.setSymbol(ArgType.INT);
        }

        this.value = Integer.parseInt(tokens[tokens.length - 1]);

    }

    /**
     * @return true if the numeric argument is negated
     */
    private boolean isNeg() {
        return isNeg;
    }

    /**
     * Compares two numeric arguments to see if they match.
     *
     * @param that numericArgument to compare with this
     * @return true if matching
     */
    boolean matches(final NumericArgument that) {
        if (!this.getName().equals(that.getName())) {
            return false;
        }
        if (this.isNeg && that.isNeg) {
            return false;
        }
        if (this.isNeg || that.isNeg) {
            return (this.value != that.value);
        }

        switch (this.getSymbol()) {
            case EQ:
                switch (that.getSymbol()) {
                    case EQ:
                        return this.value == that.value;
                    case GT:
                        return this.value > that.value;
                    case LT:
                        return this.value < that.value;
                    default:
                        return false;
                }
            case GT:
                switch (that.getSymbol()) {
                    case EQ:
                        return that.value > this.value;
                    case GT:
                        return false;
                    case LT:
                        return false;
                    default:
                        return false;
                }
            case LT:
                switch (that.getSymbol()) {
                    case EQ:
                        return that.value < this.value;
                    case GT:
                        return false;
                    case LT:
                        return false;
                    default:
                        return false;
                }
            default:
                return true;
        }
    }

    /**
     * @return the value of the numeric argument
     */
    public int getValue() {
        return value;
    }

    /**
     * Prints name (when appropriate), symbol and value.
     *
     * @return the Argument as a String.
     */
    @Override
    public String toString() {
        switch (getSymbol()) {
            case INT:
                if (!isNeg()) {
                    return "" + value;
                } else {
                    return "!" + value;
                }
            case EQ:
                if (!isNeg()) {
                    return getName() + " = " + value;
                } else {
                    return getName() + " !=" + value;
                }
            case LT:
                if (!isNeg()) {
                    return getName() + " < " + value;
                } else {
                    return getName() + " !<" + value;
                }
            case GT:
                if (!isNeg()) {
                    return getName() + " > " + value;
                } else {
                    return getName() + " !>" + value;
                }
            default:
                return super.toString();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NumericArgument that = (NumericArgument) o;

        return new EqualsBuilder()
                .append(isNeg, that.isNeg)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(isNeg)
                .append(value)
                .toHashCode();
    }
}
