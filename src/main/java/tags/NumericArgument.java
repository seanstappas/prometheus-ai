package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Subclass for arguments that have integer values
 * <p>
 * If argument has a negative value, isNeg == true.
 *  i.e. "ARG != 5" {@literal ->} this.value==5; this.isNeg==true
 */

final class NumericArgument extends Argument {

    private boolean isNeg;
    private int value;

    private boolean isNeg() {
        return isNeg;
    }

    /**
     * Compares two numeric arguments to see if they match
     *
     * @param that numericArgument to compare with this
     * @return true if matching
     */

    boolean matches(NumericArgument that) {
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
                }
            case GT:
                switch (that.getSymbol()) {
                    case EQ:
                        return that.value > this.value;
                    case GT:
                        return false;
                    case LT:
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
            this.setSymbol(ArgTypes.EQ);
        } else if (string.contains(">")) {
            this.setSymbol(ArgTypes.GT);
        } else if (string.contains("<")) {
            this.setSymbol(ArgTypes.LT);
        } else {
            this.setSymbol(ArgTypes.INT);
        }

        this.value = Integer.parseInt(tokens[tokens.length - 1]);

    }

    /**
     * Prints name (when appropriate), symbol and value
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
                } else return getName() + " !=" + value;
            case LT:
                if (!isNeg()) {
                    return getName() + " < " + value;
                } else return getName() + " !<" + value;
            case GT:
                if (!isNeg()) {
                    return getName() + " > " + value;
                } else return getName() + " !>" + value;
            default:
                return super.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NumericArgument that = (NumericArgument) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(isNeg, that.isNeg)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(isNeg)
                .append(value)
                .toHashCode();
    }

    public void setNeg(boolean neg) {
        isNeg = neg;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
