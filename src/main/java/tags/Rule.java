package tags;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a rule in the expert system.
 * <p>
 * Rules are many-to-many structures with Facts as inputs and Predicates (Facts
 * and Recommendations) as outputs. They only activate when all the input Facts
 * are active.
 */
public final class Rule extends Tag {
    private final Set<Fact> inputFacts;
    private final Set<Predicate> outputPredicates;

    /**
     * {@code confidenceValue} defaults to 1.0.
     *
     * @param inputFacts       the input Facts
     * @param outputPredicates the output Predicates
     * @see #Rule(Set, Set, double)
     */
    public Rule(final Fact[] inputFacts, final Predicate[] outputPredicates) {
        this(new HashSet<>(Arrays.asList(inputFacts)),
                new HashSet<>(Arrays.asList(outputPredicates)), 1.0);
    }

    /**
     * {@code confidenceValue} defaults to 1.0.
     *
     * @param inputFacts       the input Facts
     * @param outputPredicates the output Predicates
     * @see #Rule(Set, Set, double)
     */
    public Rule(final Set<Fact> inputFacts,
                final Set<Predicate> outputPredicates) {
        this(inputFacts, outputPredicates, 1.0);
    }

    /**
     * Creates a Rule from a set of input Facts and output Predicates.
     * <p>
     * Confidence value of output tags set to the product of the confidence
     * value of input tags.
     *
     * @param inputFacts       The condition facts of the rule.
     * @param outputPredicates The output predicates of the rule.
     * @param confidence       The confidence value of the Rule.
     */
    public Rule(final Set<Fact> inputFacts,
                final Set<Predicate> outputPredicates,
                final double confidence) {
        this.inputFacts = new HashSet<>(inputFacts);
        this.outputPredicates = new HashSet<>(outputPredicates);
        this.setConfidence(confidence);

        setOutputFactsConfidenceValue();
    }

    /**
     * Creates a Rule from string Arrays.
     *
     * @param inputFacts       The input Facts, in String form.
     * @param outputPredicates The output Tags, in String form.
     * @param confidence       The confidence value of the Rule.
     */
    private Rule(final String[] inputFacts, final String[] outputPredicates,
                 final double confidence) {
        this.inputFacts = new HashSet<>();
        this.outputPredicates = new HashSet<>();
        for (final String inputFact : inputFacts) {
            this.inputFacts.add(new Fact(inputFact));
        }
        for (final String outputPredicate : outputPredicates) {
            addOutputPredicate(outputPredicate);
        }
        this.setConfidence(confidence);
    }

    /**
     * {@code confidenceValue} defaults to 1.0.
     *
     * @param inputFacts  the input Facts
     * @param outputFacts the output Facts
     * @see #Rule(String[], String[], double)
     */
    public Rule(final String[] inputFacts, final String[] outputFacts) {
        this(inputFacts, outputFacts, 1.0);
    }

    /**
     * Create a single rule from a string.
     *
     * @param string the Rule as a string.
     * @see #makeRules(String)
     */
    public Rule(final String string) {
        this.outputPredicates = new HashSet<>();
        final List<String> tokens =
                new ArrayList<>(Arrays.asList(string.split(" ")));
        final int outputFactIndex = tokens.indexOf("->");

        for (final String outputFact : tokens
                .subList(outputFactIndex + 1, tokens.size())) {
            this.addOutputPredicate(outputFact);
        }

        final Fact[] arrInputFacts = new Fact[outputFactIndex];
        for (int i = 0; i < arrInputFacts.length; i++) {
            final Fact fact = new Fact(tokens.get(i));
            arrInputFacts[i] = fact;
        }

        this.inputFacts = new HashSet<>(Arrays.asList(arrInputFacts));
        this.setConfidence(1.0);
    }

    /**
     * Creates Rule(s) from a single String.
     * <p>
     * Example of a rule string:
     * <p>
     * "P1(ARG1,ARG2) P2(ARG3) {@literal ->} @P3(ARG4,ARG5,ARG6)"
     * <p>
     * NB: 1. Tags are separated by " " 2. arguments with a fact tag are
     * separated by "," 3. all facts on the left of {@literal "->"} are input
     * facts, on the right are output tags 4. if an output tag is preceeded by
     * "@", it is a recommendation, otherwise it is a fact
     * <p>
     * NB: rules may include OR: "P1(ARG1,ARG2) P2(ARG3) OR P3(ARG4,ARG5)
     * P4(ARG6,ARG7) {@literal ->} @P3(ARG4,ARG5,ARG6)" in this case, two rules
     * are returned "P1(ARG1,ARG2) P2(ARG3) {@literal ->} @P3(ARG4,ARG5,ARG6)"
     * and "P3(ARG4,ARG5) P4(ARG6,ARG7) {@literal ->} @P3(ARG4,ARG5,ARG6)"
     *
     * @param value the Rule as string.
     * @return List of Rules.
     */
    public static List<Rule> makeRules(final String value) {
        final List<String> tokens =
                new ArrayList<>(Arrays.asList(value.split(" ")));
        final List<Rule> ruleList = new ArrayList<>();

        final int outputPredicateIndex = tokens.indexOf("->");

        final Predicate[] outputPredicates =
                makeOutputPredicates(tokens, outputPredicateIndex);

        final List<Fact> inputFactList = new ArrayList<>();

        for (int i = 0; i < outputPredicateIndex + 1; i++) {
            if (!tokens.get(i).equals("OR") && !tokens.get(i).equals("->")) {
                final Fact predicate = new Fact(tokens.get(i));
                inputFactList.add(predicate);
            } else {
                final Fact[] inputFacts =
                        inputFactList.toArray(new Fact[inputFactList.size()]);
                final Rule rule = new Rule(inputFacts, outputPredicates);
                ruleList.add(rule);

                inputFactList.clear();
            }
        }
        return ruleList;
    }

    /**
     * Makes an array of output Predicates.
     *
     * @param tokens               the tokens
     * @param outputPredicateIndex the index of the output predicate
     * @return the array of output Predicates.
     */
    private static Predicate[] makeOutputPredicates(
            final List<String> tokens,
            final int outputPredicateIndex) {
        final Predicate[] outputPredicates =
                new Predicate[tokens.size() - outputPredicateIndex - 1];

        for (int i = outputPredicateIndex + 1; i < tokens.size(); i++) {
            if (!tokens.get(i).contains("@")) {
                final Fact fact = new Fact(tokens.get(i));
                outputPredicates[i - outputPredicateIndex - 1] = fact;
            } else {
                final Recommendation rec = new Recommendation(tokens.get(i));
                outputPredicates[i - outputPredicateIndex - 1] = rec;
            }
        }
        return outputPredicates;
    }

    /**
     * @return the input Facts
     */
    public Set<Fact> getInputFacts() {
        return Collections.unmodifiableSet(inputFacts);
    }

    /**
     * @return the output Predicates
     */
    public Set<Predicate> getOutputPredicates() {
        return Collections.unmodifiableSet(outputPredicates);
    }

    /**
     * Sets confidence value of output tags to the product of the confidence
     * value of input tags.
     */
    private void setOutputFactsConfidenceValue() {
        double value = 1.0;
        for (final Fact fact : this.inputFacts) {
            value = value * fact.getConfidence();
        }
        for (final Predicate outputPredicate : this.outputPredicates) {
            outputPredicate.setConfidence(value);
        }
    }

    /**
     * Adds an output predicate.
     *
     * @param outputPredicate the output Predicate to add
     */
    private void addOutputPredicate(final String outputPredicate) {
        if (!outputPredicate.contains("@")) {
            final Fact p = new Fact(outputPredicate);
            this.outputPredicates.add(p);
        } else {
            final Recommendation p = new Recommendation(outputPredicate);
            this.outputPredicates.add(p);
        }
    }

    /**
     * Prints Rule as inputFacts, outputPredicates, and confidenceValue
     * <p>
     * i.e. "{[[P1() 100%] [P2() 100%]] {@literal ->} [@P4 100%] 100%}"
     *
     * @return Rule as string
     */
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

        final Rule rule = (Rule) o;

        return new EqualsBuilder()
                .append(inputFacts, rule.inputFacts)
                .append(outputPredicates, rule.outputPredicates)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(inputFacts)
                .append(outputPredicates)
                .toHashCode();
    }

    @Override
    String simpleToString() {
        return MessageFormat.format(
                "{0} -> {1}",
                inputFacts,
                outputPredicates);
    }
}
