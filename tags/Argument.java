package tags;

public class Argument {

    String variable;
    argTypes symbol;

    public enum argTypes {
        STRING, EQ, GT, LT, MATCHONE, VAR
    }

    public boolean matches(Argument that) { return true; }

    Argument() {
    }

    /**
     * Constructor for Argument
     * @param string
     */

    public Argument(String string) {

        String[] tokens = string.split("[=><]");
        this.variable = tokens[0];

        if (tokens[0].equals("?")) {
            this.symbol = argTypes.MATCHONE;
        } else if (tokens[1].charAt(0) == '&') {
            this.symbol = argTypes.VAR;
        }
    }

    public Argument(String string, String[] tokens) {

        this.variable = tokens[0];

        if (tokens[0].equals("?")) {
            this.symbol = argTypes.MATCHONE;
        } else if (tokens[1].charAt(0) == '&') {
            this.symbol = argTypes.VAR;
        }
    }
}

/**
 * Subclass for arguments that have integer values
 */

class numericArgument extends Argument {

    private boolean isNeg;
    private int value;


    public boolean isNeg() {
        return isNeg;
    }

    public int getValue() {
        return value;
    }


    public boolean matches(numericArgument that) {
        if (!this.variable.equals(that.variable)) {
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
                        return this.value > that.value;
                    case GT:
                        return false;
                    case LT:
                        return false;
                }
            case LT:
                switch (that.symbol) {
                    case EQ:
                        return this.value < that.value;
                    case GT:
                        return false;
                    case LT:
                        return false;
                }
            default: return true;
        }
    }

    numericArgument(String string, String [] tokens) {

        super(string);
        this.isNeg = (string.contains("!"));

        if (string.contains("=")) {
            this.symbol = Argument.argTypes.EQ;
        } else if (string.contains(">")) {
            this.symbol = Argument.argTypes.GT;
        } else if (string.contains("<")) {
            this.symbol = Argument.argTypes.LT;
        }

        this.value = Integer.parseInt(tokens[1]);

    }
}

/**
 * Subclass for arguments that have string values
 */

class stringArgument extends Argument {

    private boolean isNeg;
    private String value;

    public boolean isNeg() {
        return isNeg;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(stringArgument that) {
        if (!this.variable.equals(that.variable)) {
            return false;
        }
        if (this.isNeg && that.isNeg) {
            return false;
        }
        if (this.isNeg || that.isNeg) {
            return (!this.value.equals(that.value));
        }
        else {
            return this.value.equals(that.value);
        }
    }

    stringArgument(String string, String [] tokens) {

        super(string);

        isNeg = (string.contains("!"));
        value = tokens[1];
        symbol = argTypes.STRING;

        }
    }

