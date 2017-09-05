package tags;

import java.util.HashMap;

/**
 * Subclass for Variable Return object returned by Fact.matches methods.
 */

public class VariableReturn {
    /**
     * True if two facts match
     */
    public boolean doesMatch;
    /**
     * `pairs` specifies a variable argument that may be replaced:
     * k: name of argument to replace, v: argument to replace it with
     */
    public HashMap<String, Argument> pairs;

    /**
     * Constructor for variable return object.
     * <p>
     * Initialises pairs hashmap.
     */
    public VariableReturn() {
        pairs = new HashMap<>();
    }

}
