package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a recommendation in the Expert System. Recommendations are for
 * specific actions to be taken (walk, stop, etc.).
 * <p>
 * Recommendations are composed of a predicate name and a set of arguments:
 *
 * @P(ARG1, ARG2, ...)
 */
public class Recommendation extends Predicate {
  /**
   * Constructs a Recommendation object from a string
   * <p>
   * NB: There should be no space characters between the arguments in a string
   * i.e. "@P(ARG1,ARG2,ARG3...)" Recommendation strings begin with "@"
   * character. Arguments are delimited by commas within parenthesis.
   *
   * @param value           String input
   * @param confidenceValue double in range [0,1] i.e. 0.n representing n0% confidence
   */

  public Recommendation(String value, double confidenceValue) {

    String[] tokens = value.split("[(),]");

    this.predicateName = tokens[0].replace("@", "");
    this.arguments = argStringParser(tokens);
    this.confidence = confidenceValue;
  }

  /**
   * {@code confidenceValue} defaults to 1.0
   *
   * @param value the Recommendation String value
   * @see #Recommendation(String, double)
   */
  public Recommendation(String value) {
    this(value, 1.0);
  }

  Recommendation(String predicateName, List<Argument> arguments,
                 double confidence) {
    this.predicateName = predicateName;
    this.arguments = arguments;
    this.confidence = confidence;
  }

  /**
   * Calls the appropriate Argument constructor on a string token
   * <p>
   *
   * @param argString String token
   * @return A single argument
   * @see Fact#makeArgument(String)
   */

  private static Argument makeArgument(String argString) {
    return Fact.makeArgument(argString);
  }

  @Override
  Predicate getPredicateCopy() {
    return new Recommendation(predicateName, arguments, confidence);
  }

  /**
   * Parses a raw string into a list of string tokens that represent each
   * argument
   *
   * @param tokens string input
   * @return list of string arguments
   */

  private List<Argument> argStringParser(String[] tokens) {
    List<Argument> argSet = new ArrayList<>();
    for (int i = 1; i < tokens.length; i++) {
      Argument argument = makeArgument(tokens[i]);
      argSet.add(argument);
    }
    return argSet;
  }

  public String getPredicateName() {
    return predicateName;
  }

  public List<Argument> getArguments() {
    return Collections.unmodifiableList(arguments);
  }

  /**
   * Prints predicate name, arguments and confidence value of recommendation
   * <p>
   * e.g. "[@P(ARG1, ARG2) 100%]"
   *
   * @return string value of Recommendation
   */
  @Override
  public String toString() {
    return simpleToString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Recommendation that = (Recommendation) o;

    return new EqualsBuilder()
        .append(predicateName, that.predicateName)
        .append(arguments, that.arguments)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(predicateName)
        .append(arguments)
        .toHashCode();
  }

  @Override
  String simpleToString() {
    return MessageFormat.format(
        "@{0}{1}",
        predicateName,
        arguments);
  }
}

