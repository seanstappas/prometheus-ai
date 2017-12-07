package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Subclass for arguments that have variable values
 * <p>
 * i.e. "?", "*" or {@literal "&x"}
 */

final class VariableArgument extends Argument {

    /**
     * Constructor of variable Arguments.
     * <p>
     * Arguments must be a string made up of alpha characters, as well as one of
     * ["*", "?", {@literal &}] characters
     *
     * @param string argument as a string
     * @param tokens argument as tokens, split on mathematical symbols
     */
    VariableArgument(final String string, final String[] tokens) {
        super(tokens);

        if (tokens[0].equals("*")) {
            this.setSymbol(ArgType.MATCHALL);
            this.setName("*");
        } else if (tokens[0].equals("?")) {
            this.setSymbol(ArgType.MATCHONE);
            this.setName("?");
        } else if (tokens[0].charAt(0) == '&') {
            this.setSymbol(ArgType.VAR);
            this.setName(tokens[0]);
        }
    }

    /**
     * Prints name (when appropriate), and type.
     *
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

    /**
     * Equals method. Only override this method if additional fields are added
     * to the subclass.
     *
     * @param o the object to compare to.
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Argument argument = (Argument) o;

        return new EqualsBuilder()
                .append(getName(), argument.getName())
                .append(getSymbol(), argument.getSymbol())
                .isEquals();
    }

    /**
     * Hashcode method. Only override this method if additional fields are added
     * to the subclass.
     *
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getName())
                .append(getSymbol())
                .toHashCode();
    }
}
