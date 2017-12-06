package tags;

import java.util.List;
import java.util.Map;

/**
 * Interface for Predicates (Implemented by Fact and Recommendation)
 */

public abstract class Predicate extends Tag {
  String predicateName;
  List<Argument> arguments;

  public abstract String getPredicateName();

  public abstract List<Argument> getArguments();

  abstract Predicate getPredicateCopy();

  /**
   * Returns a copy of the current Predicate with replaced variable argument(s) with a String or Numeric Argument.
   *
   * @param pendingReplacementPairs the pending replacement pairs
   * @return a copy of the current Predicate with replaced variable argument(s) with a String or Numeric Argument.
   */
  public Predicate replaceVariableArguments(Map<String, Argument> pendingReplacementPairs) {
    Predicate p = getPredicateCopy();
    int argumentIndex = 0;
    for (Argument argument : p.arguments) {
      if (pendingReplacementPairs.containsKey(argument.getName())) {
        arguments.set(argumentIndex, pendingReplacementPairs.get(argument.getName()));
      }
      argumentIndex++;
    }
    return p;
  }
}
