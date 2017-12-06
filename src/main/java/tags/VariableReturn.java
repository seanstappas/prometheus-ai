package tags;

import java.util.HashMap;

/**
 * Subclass for Variable Return object returned by Fact.getMatchResult methods.
 */

public class VariableReturn {
  /**
   * True if two facts match
   */
  private boolean factMatch;
  /**
   * `pairs` specifies a variable argument that may be replaced:
   * k: name of argument to replace, v: argument to replace it with
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


  public boolean isFactMatch() {
    return factMatch;
  }

  public void setFactMatch(boolean factMatch) {
    this.factMatch = factMatch;
  }


  public HashMap<String, Argument> getPairs() {
    return pairs;
  }

  public void setPairs(HashMap<String, Argument> pairs) {
    this.pairs = pairs;
  }
}
