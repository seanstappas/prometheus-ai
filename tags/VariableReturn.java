package tags;

public class VariableReturn {
    public boolean doesMatch;
    public Argument argumentToMatch;
    public Argument argumentThatReplaces;

    public VariableReturn() {
    }

    public VariableReturn(boolean dm, Argument atr, Argument atm) {
        this.doesMatch = dm;
        this.argumentToMatch = atm;
        this.argumentThatReplaces = atr;
    }
}
