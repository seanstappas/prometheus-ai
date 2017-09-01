package tags;

/**
 * Arguments are composed of a name and symbol (symbol types outlined below)
 * <p>
 * STRING: argument is made up of a string (e.g. tall)
 * EQ: argument is a name equal to an integer (e.g. height = 10)
 * GT: argument is a name greater than an integer (e.g. height > 10)
 * LT: argument is a name less that an integer (e.g. height < 10)
 * MATCHONE: argument matches on a corresponding argument in a fact with same predicate name (see BASH '?')
 * VAR: argument is a variable integer (e.g. height = &x)
 * MATCHALL: argument matches on >0 arguments in a fact with same predicate name (see BASH '*')
 * INT: argument is made up of an integer value (e.g. 10)
 */

public class Argument {

    String name;
    argTypes symbol;

    public enum argTypes {
        STRING, EQ, GT, LT, MATCHONE, VAR, MATCHALL, INT
    }

    public Argument() {
    }

    public String getName() {
        return name;
    }

    public argTypes getSymbol() {
        return symbol;
    }

    /**
     * Constructor for Argument
     * <p>
     * Attempts to split an argument string (e.g. height=10) on the mathematical symbol (in this case 10),
     * assigns name to first token, if there are multiple tokens
     *
     */

    public Argument(String[] tokens) {
        if (tokens.length > 1) {
            this.name = tokens[0];
        } else {
            this.name = "";
        }
    }

    /**
     * Compares two arguments, calling appropriate overloaded method
     *
     * @param inputFact
     * @return true if two arguments match
     */

    boolean matches(Argument inputFact) {
        switch (this.symbol) {
            case STRING:
                if (inputFact.symbol.equals(argTypes.VAR)) {
                    return true;
                }
                if (inputFact.symbol.equals(argTypes.STRING) || inputFact.symbol.equals(argTypes.VAR)) {
                    return ((StringArgument) this).matches((StringArgument) inputFact);
                }
                return (inputFact.symbol.equals(argTypes.MATCHONE));
            case EQ:
            case GT:
            case LT:
            case INT:
                if (inputFact.symbol.equals(argTypes.VAR)) {
                    return true;
                }
                if (inputFact.symbol.equals(argTypes.INT) ||
                        inputFact.symbol.equals(argTypes.EQ) ||
                        inputFact.symbol.equals(argTypes.LT) ||
                        inputFact.symbol.equals(argTypes.GT)) {
                    return ((NumericArgument) this).matches((NumericArgument) inputFact);
                }
                return (inputFact.symbol.equals(argTypes.MATCHONE));
            case MATCHONE:
                return !inputFact.symbol.equals(argTypes.VAR);
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
}

/**
 * Subclass for arguments that have integer values
 */

class NumericArgument extends Argument {

    private boolean isNeg;
    private int value;


    public boolean isNeg() {
        return isNeg;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

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

    NumericArgument(String string, String[] tokens) {

        super(tokens);
        this.isNeg = (string.contains("!"));

        if (string.contains("=")) {
            this.symbol = Argument.argTypes.EQ;
        } else if (string.contains(">")) {
            this.symbol = Argument.argTypes.GT;
        } else if (string.contains("<")) {
            this.symbol = Argument.argTypes.LT;
        } else {
            this.symbol = Argument.argTypes.INT;
        }

        this.value = Integer.parseInt(tokens[tokens.length - 1]);

    }

    @Override
    public String toString() {
        switch (symbol) {
            case INT:
                return "" + value;
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

}

/**
 * Subclass for arguments that have string values
 */

class StringArgument extends Argument {

    private boolean isNeg;
    private String value;

    public boolean isNeg() {
        return isNeg;
    }

    public String getValue() {
        return value;
    }

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

    StringArgument(String string, String[] tokens) {

        super(tokens);

        isNeg = (string.contains("!"));
        value = tokens[tokens.length - 1];
        symbol = argTypes.STRING;

    }

    @Override
    public String toString() {
        switch (symbol) {
            case STRING:
                return "" + value;
            case EQ:
                if (!isNeg()) {
                    return name + " =" + value;
                } else {
                    return name + " !=" + value;
                }
            default:
                return super.toString();
        }
    }
}

/**
 * Subclass for arguments that have variable values
 * TODO: Redo VAR TYPE
 */

class VariableArgument extends Argument {

    VariableArgument(String string, String[] tokens) {

        super(tokens);

        if (tokens[0].equals("*")) {
            this.symbol = argTypes.MATCHALL;
            this.name = "*";
        } else if (tokens[0].equals("?")) {
            this.symbol = argTypes.MATCHONE;
            this.name = "?";
        } else if (tokens[0].charAt(0) == '&') {
            this.symbol = argTypes.VAR;
            this.name = tokens[0];
        }
    }

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

