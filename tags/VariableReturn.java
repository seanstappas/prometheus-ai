package tags;

import java.util.HashMap;

public class VariableReturn {
    public boolean doesMatch;
    public String argumentNameToMatch;
    public Argument argumentThatReplaces;

    public HashMap<String, Argument> pairs;
    public VariableReturn() {
        pairs = new HashMap<>();
    }

}
