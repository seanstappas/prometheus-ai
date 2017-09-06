package tags;

import java.util.*;

/**
 * Represents a rule in the expert system.
 * <p>
 * Rules are many-to-many structures with Facts as inputs and Predicates (Facts and
 * Recommendations) as outputs.
 * They only activate when all the input Facts are active.
 */
public class Rule extends Tag {
    private Set<Fact> inputFacts;
    private Set<IPredicate> outputPredicates;

    public Set<Fact> getInputFacts() {
        return inputFacts;
    }

    public Set<IPredicate> getOutputPredicates() {
        return outputPredicates;
    }

    /**
     * Sets confidence value of output tags to the product of the confidence value of input tags.
     */

    private void setoutputFactsConfidenceValue() {
        double value = 1.0;
        for (Fact fact : this.inputFacts) {
            value = value * fact.getConfidenceValue();
        }
        for (IPredicate outputPredicate : this.outputPredicates) {
            outputPredicate.setConfidenceValue(value);
        }
    }

    /**
     * {@code confidenceValue} defaults to 1.0
     * @see #Rule(Set, Set, double)
     */

    public Rule(Fact[] inputFacts, IPredicate[] outputPredicate) {
        this(new HashSet<>(Arrays.asList(inputFacts)), new HashSet<>(Arrays.asList(outputPredicate)), 1.0);
    }

    /**
     * {@code confidenceValue} defaults to 1.0
     *
     * @see #Rule(Set, Set, double)
     */

    public Rule(Set<Fact> inputFacts, Set<IPredicate> outputPredicate) {
        this(inputFacts, outputPredicate, 1.0);
    }

    /**
     * Creates a Rule from a set of input Facts and output Predicates.
     * <p>
     * Confidence value of output tags set to the product of the confidence value of input tags.
     *
     * @param inputFacts       The condition facts of the rule.
     * @param outputPredicates The output predicates of the rule.
     * @param confidenceValue  The confidence value of the Rule.
     */

    private Rule(Set<Fact> inputFacts, Set<IPredicate> outputPredicates, double confidenceValue) {
        this.inputFacts = new HashSet<>(inputFacts);
        this.outputPredicates = new HashSet<>(outputPredicates);
        this.type = TagType.RULE;
        this.setConfidenceValue(confidenceValue);

        setoutputFactsConfidenceValue();
    }

    /**
     * {@code confidenceValue} defaults to 1.0
     * @see #Rule(Set, Set, double)
     */

    public Rule() {
        this.inputFacts = new HashSet<>();
        this.outputPredicates = new HashSet<>();
        this.type = TagType.RULE;
        this.setConfidenceValue(1.0);
    }

    /**
     * Creates a Rule from string Arrays
     * @param inputFacts The input Facts, in String form.
     * @param outputPredicates The output Tags, in String form.
     * @param confidenceValue The confidence value of the Rule.
     */
    private Rule(String[] inputFacts, String[] outputPredicates, double confidenceValue) {
        this.inputFacts = new HashSet<>();
        this.outputPredicates = new HashSet<>();
        for (String inputFact : inputFacts) {
            this.inputFacts.add(new Fact(inputFact));
        }
        for (String outputPredicate : outputPredicates) {
            if (!outputPredicate.contains("@")) {
                Fact p = new Fact(outputPredicate);
                this.outputPredicates.add(p);
            } else {
                Recommendation p = new Recommendation(outputPredicate);
                this.outputPredicates.add(p);
            }
        }
        this.type = TagType.RULE;
        this.setConfidenceValue(confidenceValue);
    }

    /**
     * {@code confidenceValue} defaults to 1.0
     *
     * @see #Rule(String[], String[], double)
     */

    public Rule(String[] inputFacts, String[] outputFacts) {
        this(inputFacts, outputFacts, 1.0);
    }

    /**
     * Creates Rule(s) from a single String.
     * <p>
     * Example of a rule string:
     * <p>
     * "P1(ARG1,ARG2) P2(ARG3) {@literal ->} @P3(ARG4,ARG5,ARG6)"
     * <p>
     * NB: 1. Tags are separated by " " 2. arguments with a fact tag are separated by ","
     * 3. all facts on the left of {@literal "->"} are input facts, on the right are output tags
     * 4. if an output tag is preceeded by "@", it is a recommendation, otherwise it is a fact
     *<p>
     * NB: rules may include OR: "P1(ARG1,ARG2) P2(ARG3) OR P3(ARG4,ARG5) P4(ARG6,ARG7) {@literal ->} @P3(ARG4,ARG5,ARG6)"
     *      in this case, two rules are returned "P1(ARG1,ARG2) P2(ARG3) {@literal ->} @P3(ARG4,ARG5,ARG6)" and
     *          "P3(ARG4,ARG5) P4(ARG6,ARG7) {@literal ->} @P3(ARG4,ARG5,ARG6)"
     *
     * @param value the Rule as string.
     * @return List of Rules.
     */

    public static List<Rule> makeRules(String value) {
        List<String> tokens = new ArrayList<>(Arrays.asList(value.split(" ")));
        int outputFactIndex = tokens.indexOf("->");

        IPredicate[] outputIPredicates = new IPredicate[tokens.size() - outputFactIndex - 1];

        for (int i = outputFactIndex + 1; i < tokens.size(); i++) {
                if (!tokens.get(i).contains("@")) {
                    Fact fact = new Fact(tokens.get(i));
                    outputIPredicates[i - outputFactIndex-1] = fact;
                } else {
                    Recommendation rec = new Recommendation(tokens.get(i));
                    outputIPredicates[i - outputFactIndex-1] = rec;
                }
        }

        List<Rule> ruleList = new ArrayList<>();
        List<Fact> inputFactList = new ArrayList<>();

        for (int i = 0; i < outputFactIndex + 1; i++) {
            if (!tokens.get(i).equals("OR") & !tokens.get(i).equals("->")) {
                Fact IPredicate = new Fact(tokens.get(i));
                inputFactList.add(IPredicate);
            }
            else {
                Fact[] inputFacts = inputFactList.toArray(new Fact[inputFactList.size()]);
                Rule rule = new Rule(inputFacts, outputIPredicates);
                ruleList.add(rule);

                inputFactList.clear();
            }
        }
        return ruleList;
    }


    /**
     * Create a single rule from a string
     * @see #makeRules(String)
     * @param string the Rule as a string.
     */

    public Rule(String string) {
        List<String> tokens = new ArrayList<>(Arrays.asList(string.split(" ")));
        int outputFactIndex = tokens.indexOf("->");

        List<IPredicate> outputPredicateList = new ArrayList<>();
        for (String outputFact : tokens.subList(outputFactIndex + 1, tokens.size())) {
            if (!outputFact.contains("@")) {
                Fact fact = new Fact(outputFact);
                outputPredicateList.add(fact);
            } else {
                Recommendation rec = new Recommendation(outputFact);
                outputPredicateList.add(rec);
            }
        }

        Fact[] inputFacts = new Fact[outputFactIndex];
        for (int i = 0; i < inputFacts.length; i++) {
            Fact fact = new Fact(tokens.get(i));
            inputFacts[i] = fact;
        }

        IPredicate[] outputPredicate = outputPredicateList.toArray(new IPredicate[outputPredicateList.size()]);
        this.inputFacts = new HashSet<>(Arrays.asList(inputFacts));
        this.outputPredicates = new HashSet<>(Arrays.asList(outputPredicate));
        this.type = TagType.RULE;
        this.setConfidenceValue(1.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Rule rule = (Rule) o;

        if (inputFacts != null ? !inputFacts.equals(rule.inputFacts) : rule.inputFacts != null) return false;
        return outputPredicates != null ? outputPredicates.equals(rule.outputPredicates) : rule.outputPredicates == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (inputFacts != null ? inputFacts.hashCode() : 0);
        result = 31 * result + (outputPredicates != null ? outputPredicates.hashCode() : 0);
        return result;
    }


    /**
     * Prints Rule as inputFacts, outputPredicates, and confidenceValue
     * <p>
     * i.e. "{[[P1() 100%] [P2() 100%]] {@literal ->} [@P4 100%] 100%}"
     * @return Rule as string
     */
    @Override
    public String toString() {
        return "{ " + inputFacts + "=>" + outputPredicates + getConfidenceValue() * 100 + "% }";
    }

}
