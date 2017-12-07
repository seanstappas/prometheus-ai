package tags;

import java.util.HashMap;

/**
 * Subclass for Variable Return object returned by Fact.getMatchResult methods.
 */

public class VariableReturn {
    /**
     * True if two facts match.
     */
    private boolean factMatch;
    /**
     * `pairs` specifies a variable argument that may be replaced:
     * k: name of argument to replace, v: argument to replace it with.
     */
    private HashMap<String, Argument> pairs;

    /**
     * Constructor for variable return object.
     * <p>
     * Initialises pairs hashmap.
     */
    public VariableReturn() {
        pairs = new HashMap<>();
    }


    /**
     * @return true if the Facts match
     */
    public final boolean isFactMatch() {
        return factMatch;
    }

    /**
     * Sets if the Facts match.
     *
     * @param factMatch true if the Facts match
     */
    final void setFactMatch(final boolean factMatch) {
        this.factMatch = factMatch;
    }

    /**
     * @return the pairs of the variable return
     */
    public final HashMap<String, Argument> getPairs() {
        return pairs;
    }

    /**
     * Sets the variable pairs.
     *
     * @param pairs the variable pairs
     */
    final void setPairs(final HashMap<String, Argument> pairs) {
        this.pairs = pairs;
    }
}
