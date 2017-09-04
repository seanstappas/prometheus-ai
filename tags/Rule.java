package tags;

import java.util.*;

/**
 * Represents a rule in the expert system. Rules are many-to-many structures with Facts as inputs and Facts and
 * Recommendations as outputs. They only activate when all the input Tags are active.
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

    public double getConfidenceValue() {
        return confidenceValue;
    }

    public void setConfidenceValue(double confidenceValue) {
        this.confidenceValue = confidenceValue;
    }

    /**
     * Confidence value of output tags set to the product of the confidence value of input tags
     */

    public void setoutputFactsConfidenceValue() {
        double value = 1.0;
        for (Fact fact : this.inputFacts) {
            value = value * fact.getConfidenceValue();
        }
        for (IPredicate outputPredicate : this.outputPredicates) {
            outputPredicate.setConfidenceValue(value);
        }
    }

    /**
     * Creates a Rule.
     *
     * @param inputFacts condition facts of the rule
     * @param outputIPredicate  outputFacts tag of the rule
     */
    public Rule(Fact[] inputFacts, IPredicate[] outputIPredicate) {
        this(new HashSet<>(Arrays.asList(inputFacts)), new HashSet<>(Arrays.asList(outputIPredicate)), 1.0);
    }

    public Rule(Set<Fact> inputFacts, Set<IPredicate> outputPredicate) {
        this(inputFacts, outputPredicate, 1.0);
    }

    public Rule(Set<Fact> inputFacts, Set<IPredicate> outputIPredicates, double confidenceValue) {
        this.inputFacts = new HashSet<>(inputFacts);
        this.outputPredicates = new HashSet<>(outputIPredicates);
        this.type = TagType.RULE;
        this.confidenceValue = confidenceValue;

        setoutputFactsConfidenceValue();
    }

    public Rule() {
        this.inputFacts = new HashSet<>();
        this.outputPredicates = new HashSet<>();
        this.type = TagType.RULE;
        this.confidenceValue = 1.0;
    }

    /**
     * Creates a Rule from Strings, assuming all Tags (input and output) are of the provided TagType.
     *
     * @param inputFacts the input Facts, in String form
     * @param outputFacts the output Tags, in String form
     * @param type       the Type of output Tags
     */
    public Rule(String[] inputFacts, String[] outputFacts, TagType type, double confidenceValue) {
        this.inputFacts = new HashSet<>();
        this.outputPredicates = new HashSet<>();
        for (String inputFact : inputFacts) {
            this.inputFacts.add(new Fact(inputFact));
        }
        for (String outputPredicate : outputFacts) {
            if (!outputPredicate.contains("@")) {
                Fact p = new Fact(outputPredicate);
                this.outputPredicates.add(p);
            } else {
                Recommendation p = new Recommendation(outputPredicate);
                this.outputPredicates.add(p);
            }
        }
        this.type = TagType.RULE;
        this.confidenceValue = confidenceValue;
    }

    public Rule(String[] inputFacts, String[] outputFacts, TagType type) {
        this(inputFacts, outputFacts, type, 1.0);
    }

    /**
     * Creates Rule(s) from a single String.
     * <p>
     * Example of a rule string:
     * <p>
     * "P1(ARG1,ARG2) P2(ARG3) -> @P3(ARG4,ARG5,ARG6)"
     * <p>
     * NB: 1. Tags are separated by " " 2. arguments with a fact tag are separated by ","
     * 3. all facts on the left of "->" are input facts, on the right are output tags
     * 4. if an output tag is preceeded by "@", it is a recommendation, otherwise it is a fact
     *
     * NB: rules may include OR: "P1(ARG1,ARG2) P2(ARG3) OR P3(ARG4,ARG5) P4(ARG6,ARG7) -> @P3(ARG4,ARG5,ARG6)"
     *      in this case, two rules are returned "P1(ARG1,ARG2) P2(ARG3) -> @P3(ARG4,ARG5,ARG6)" and
     *          "P3(ARG4,ARG5) P4(ARG6,ARG7) -> @P3(ARG4,ARG5,ARG6)"
     *
     * @param value rule as string
     * @return list of rules
     */

    public static List<Rule> makeRules(String value) {
        List<String> tokens = new ArrayList<>(Arrays.asList(value.split(" ")));
        int outputFactIndex = tokens.indexOf("->");

        IPredicate[] outputIPredicates = new IPredicate[tokens.size() - outputFactIndex - 1];

        for (int i = outputFactIndex + 1; i < tokens.size(); i++) {
            for (int j = 0; j < outputIPredicates.length; j++) {
                if (!tokens.get(i).contains("@")) {
                    Fact IPredicate = new Fact(tokens.get(i));
                    outputIPredicates[j] = IPredicate;
                } else {
                    Recommendation rec = new Recommendation(tokens.get(i));
                    outputIPredicates[j] = rec;
                }
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
     *
     * @param string
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

    @Override
    public String toString() {
        return "{ " + inputFacts + "=>" + outputPredicates + confidenceValue * 100 + "% }";
    }

}
