package tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Subclass for arguments that have string values
 * <p>
 * If argument has a negative value, isNeg == true.
 * i.e. "ARG != big" {@literal ->} this.value.equals("big"; this.isNeg==true
 */

final class StringArgument extends Argument {

  private boolean isNeg;
  private String value;

  /**
   * Constructor of string Arguments
   * <p>
   * Arguments must be a string made up of alpha characters, can contain ["=",
   * "!"] characters
   *
   * @param string argument as a string
   * @param tokens argument as tokens, split on mathematical symbols
   */

  StringArgument(String string, String[] tokens) {

    super(tokens);

    isNeg = (string.contains("!"));
    value = tokens[tokens.length - 1];
    symbol = ArgTypes.STRING;
  }

  private boolean isNeg() {
    return isNeg;
  }

  public void setNeg(boolean neg) {
    isNeg = neg;
  }

  /**
   * Compares two string arguments to see if they match
   *
   * @param that stringArgument to compare with this
   * @return true if matching
   */

  boolean matches(StringArgument that) {
    if (!this.getName().equals(that.getName())) {
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

  /**
   * Prints name (when appropriate), symbol and value
   *
   * @return the Argument as a String.
   */

  @Override
  public String toString() {
    if (this.getName().equals("")) {
      if (!isNeg()) {
        return "" + value;
      } else {
        return "!" + value;
      }
    } else {
      if (!isNeg()) {
        return getName() + " = " + value;
      } else {
        return getName() + " != " + value;
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    StringArgument that = (StringArgument) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(isNeg, that.isNeg)
        .append(value, that.value)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(isNeg)
        .append(value)
        .toHashCode();
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
