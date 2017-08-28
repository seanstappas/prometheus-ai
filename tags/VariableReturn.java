package tags;

import java.util.HashMap;

/**
 * Object returned by matches methods for facts in ES
 * If two facts match, doesMatch == true
 *  `pairs` specifies a variable argument that may be replaced; k: name of argument to replace, v: argument to replace it with
 */

public class VariableReturn {
    public boolean doesMatch;
    public HashMap<String, Argument> pairs;
    public VariableReturn() {
        pairs = new HashMap<>();
    }

}
