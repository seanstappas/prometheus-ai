package tags;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a fact in the Expert System. Facts are calculus predicates that
 * represent something that is seen as true.
 * <p>
 * Facts are composed of a predicate name and a set of arguments: P(ARG1, ARG2,
 * ...)
 */

public final class Fact extends Predicate {
    /**
     * Constructs a Fact object from a string
     * <p>
     * NB: There should be no space characters between the arguments in a string
     * i.e. "P(ARG1,ARG2,ARG3...)". Arguments are delimited by commas within
     * parenthesis.
     *
     * @param value           String input
     * @param confidenceValue double in range [0,1] i.e. 0.n representing n0%
     *                        confidence
     */

    public Fact(final String value, final double confidenceValue) {

        final String[] tokens = value.split("[(),]");

        this.setPredicateName(tokens[0]);
        this.setArguments(argStringParser(tokens));
        this.setConfidence(confidenceValue);
    }

    /**
     * {@code confidenceValue} defaults to 1.0.
     *
     * @param value the Fact value
     * @see #Fact(String, double)
     */
    public Fact(final String value) {
        this(value, 1.0);
    }

    Fact(final String predicateName, final List<Argument> arguments,
         final double confidence) {
        this.setPredicateName(predicateName);
        this.setArguments(arguments);
        this.setConfidence(confidence);
    }

    /**
     * Calls the appropriate Argument constructor on a string token.
     * <p>
     * If argument is numeric {@literal ->} NumericArgument; If contains
     * {@literal [?*&]} {@literal ->} VariableArgument; Else {@literal
     * ->}StringArgument
     *
     * @param argString String token
     * @return A single argument
     */
    static Argument makeArgument(final String argString) {
        final String[] argTokens = argString.split("[=><!]");
        final int lastElem = argTokens.length - 1;

        if (argTokens[lastElem].matches("-?\\d+(\\.\\d+)?")) {
            return new NumericArgument(argString, argTokens);
        } else if (argTokens[lastElem].matches("[?*]")
                || argTokens[lastElem].charAt(0) == '&') {
            return new VariableArgument(argString, argTokens);
        } else {
            return new StringArgument(argString, argTokens);
        }
    }

    @Override
    Predicate getPredicateCopy() {
        return new Fact(getPredicateName(), getArguments(), getConfidence());
    }

    /**
     * Parses a raw string into a list of string tokens that represent each
     * argument.
     * <p>
     *
     * @param tokens string input
     * @return list of string arguments
     */
    private List<Argument> argStringParser(final String[] tokens) {
        final List<Argument> argSet = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            final Argument argument = makeArgument(tokens[i]);
            argSet.add(argument);
        }
        return argSet;
    }

    /**
     * Compares two facts to see if they are compatible.
     * <p>
     * If matching occurs on a variable argument, return object includes a list
     * of tuple[s].
     *
     * @param inputFact fact contained in a Rule
     * @return true if facts are 'matched' (notice not necessarily equal)
     */
    public VariableReturn getMatchResult(final Fact inputFact) {

        final VariableReturn result = new VariableReturn();
        result.setFactMatch(false);
        if (!matchPredicateName(inputFact, result)) {
            return result;
        }
        if (!matchArgumentsSize(inputFact, result)) {
            return result;
        }
        if (!matchArguments(result, inputFact.getArguments())) {
            return result;
        }
        result.setFactMatch(true);
        return result;
    }

    /**
     * Checks if the Fact matches the current fact.
     *
     * @param fact the fact to check
     * @return true if the Fact matches the current fact
     */
    public boolean matches(final Fact fact) {
        if (this.equals(fact)) {
            return true;
        }
        final VariableReturn result = new VariableReturn();
        return matchPredicateName(fact, result)
                && matchArgumentsSize(fact, result)
                && matchArguments(result, fact.getArguments());
    }

    /**
     * Checks if the predicate names match.
     *
     * @param fact   the fact to check
     * @param result the result of the matching
     * @return true if the Facts match
     */
    private boolean matchPredicateName(final Fact fact,
                                       final VariableReturn result) {
        if (!this.getPredicateName().equals(fact.getPredicateName())) {
            result.setFactMatch(false);
            return false;
        }
        return true;
    }

    /**
     * Checks if the Fact arguments match.
     *
     * @param result    the result of matching
     * @param arguments the fact arguments
     * @return true if the facts match
     */
    private boolean matchArguments(
            final VariableReturn result,
            final List<Argument> arguments) {
        final Iterator iterFact = this.getArguments().iterator();
        final Iterator iterInputFact = arguments.iterator();

        while (iterFact.hasNext()) {
            final Argument argFact = (Argument) iterFact.next();
            final Argument argInputFact = (Argument) iterInputFact.next();
            if (argFact.getSymbol().equals(Argument.ArgType.MATCHALL)
                    || argInputFact.getSymbol()
                    .equals(Argument.ArgType.MATCHALL)) {
                result.setFactMatch(true);
                return true;
            }
            if (argInputFact.getSymbol().equals(Argument.ArgType.VAR)) {
                result.setFactMatch(true);
                result.getPairs().put(argInputFact.getName(), argFact);
            }
            result.setFactMatch(argFact.matches(argInputFact));
            if (!result.isFactMatch()) {
                return false;
            }
        }
        if (iterInputFact.hasNext()) {
            final Argument argFact = (Argument) iterInputFact.next();
            result.setFactMatch(
                    (argFact.getSymbol().equals(Argument.ArgType.MATCHALL)));
            return true;
        }
        return false;
    }

    /**
     * Checks if the argument sizes match.
     *
     * @param fact   the fact to check
     * @param result the result of matching
     * @return true if the facts match
     */
    private boolean matchArgumentsSize(final Fact fact,
                                       final VariableReturn result) {
        if (fact.getArguments().size() > this.getArguments().size()) {
            for (int i = this.getArguments().size();
                 i < fact.getArguments().size();
                 i++) {
                if (!fact.getArguments().get(i).getSymbol()
                        .equals(Argument.ArgType.MATCHALL)) {
                    result.setFactMatch(false);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return simpleToString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Fact fact = (Fact) o;

        return new EqualsBuilder()
                .append(getPredicateName(), fact.getPredicateName())
                .append(getArguments(), fact.getArguments())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getPredicateName())
                .append(getArguments())
                .toHashCode();
    }

    @Override
    String simpleToString() {
        return MessageFormat.format(
                "{0}{1}",
                getPredicateName(),
                getArguments());
    }
}
