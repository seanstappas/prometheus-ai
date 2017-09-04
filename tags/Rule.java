package tags;

import java.util.*;

/**
 * Represents a rule in the expert system. Rules are many-to-many structures with Facts as inputs and Facts and
 * Recommendations as outputs. They only activate when all the input Tags are active.
 */
public class Rule extends Tag {
    private Set<Fact> inputFacts;
    private Set<Fact> outputTags;

    public Set<Fact> getInputFacts() {
        return inputFacts;
    }

    public Set<Fact> getOutputTags() {
        return outputTags;
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

    public void setOutputTagsConfidenceValue() {
        double value = 1.0;
        for (Fact fact: this.inputFacts) {
            value = value * fact.getConfidenceValue();
        }
        for (Fact fact: this.outputTags) {
            fact.setConfidenceValue(value);
        }
    }

    /**
     * Creates a Rule.
     *
     * @param inputFacts condition facts of the rule
     * @param outputTag  outputTags tag of the rule
     */
    public Rule(Fact[] inputFacts, Fact[] outputTag) {
        this(new HashSet<>(Arrays.asList(inputFacts)), new HashSet<>(Arrays.asList(outputTag)), 1.0);
    }

    public Rule(Set<Fact> inputFacts, Set<Fact> outputTag) {
        this(inputFacts, outputTag, 1.0);
    }

    public Rule(Set<Fact> inputFacts, Set<Fact> outputTags, double confidenceValue) {
        this.inputFacts = new HashSet<>(inputFacts);
        this.outputTags = new HashSet<>(outputTags);
        this.type = TagType.RULE;
        this.value = this.toString();
        this.confidenceValue = confidenceValue;

        setOutputTagsConfidenceValue();
    }

    public Rule() {
    }


    /**
     * Creates a Rule from Strings, assuming all Tags (input and output) are of the provided TagType.
     *
     * @param inputFacts the input Facts, in String form
     * @param outputTags the output Tags, in String form
     * @param type       the Type of output Tags
     */
    public Rule(String[] inputFacts, String[] outputTags, TagType type, double confidenceValue) {
        this.inputFacts = new HashSet<>();
        this.outputTags = new HashSet<>();
        for (String inputFact : inputFacts) {
            this.inputFacts.add(new Fact(inputFact));
        }
        for (String outputTag : outputTags) {
            this.outputTags.add((Fact) Tag.createTagFromString(outputTag, type)); //TODO: FIX
        }
        this.type = TagType.RULE;
        this.value = this.toString();
        this.confidenceValue = confidenceValue;
    }

    public Rule(String[] inputFacts, String[] outputTags, TagType type) {
        this(inputFacts, outputTags, type, 1.0);
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
        int outputTagIndex = tokens.indexOf("->");

        Fact[] outputTags = new Fact[tokens.size() - outputTagIndex - 1];

        for (int i = outputTagIndex + 1; i < tokens.size(); i++) {
            for (int j = 0; j < outputTags.length; j++) {
                if (!tokens.get(i).contains("@")) {
                    Fact fact = new Fact(tokens.get(i));
                    outputTags[j] = fact;
                } else {
                    Recommendation rec = new Recommendation(tokens.get(i));
                    outputTags[j] = rec;
                }
            }
        }

        List<Rule> ruleList = new ArrayList<>();
        List<Fact> inputFactList = new ArrayList<>();

        for (int i = 0; i < outputTagIndex + 1; i++) {
            if (!tokens.get(i).equals("OR") & !tokens.get(i).equals("->")) {
                Fact fact = new Fact(tokens.get(i));
                inputFactList.add(fact);
            }
            else {
                Fact[] inputFacts = inputFactList.toArray(new Fact[inputFactList.size()]);
                Rule rule = new Rule(inputFacts, outputTags);
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
        int outputTagIndex = tokens.indexOf("->");

        List<Fact> outputTagList = new ArrayList<>();
        for (String outputTag : tokens.subList(outputTagIndex + 1, tokens.size())) {
            if (!outputTag.contains("@")) {
                Fact fact = new Fact(outputTag);
                outputTagList.add(fact);
            } else {
                Recommendation rec = new Recommendation(outputTag);
                outputTagList.add(rec);
            }
        }

        Fact[] inputFacts = new Fact[outputTagIndex];
        for (int i = 0; i < inputFacts.length; i++) {
            Fact fact = new Fact(tokens.get(i));
            inputFacts[i] = fact;
        }

        Fact[] outputTag = outputTagList.toArray(new Fact[outputTagList.size()]);
        this.inputFacts = new HashSet<>(Arrays.asList(inputFacts));
        this.outputTags = new HashSet<>(Arrays.asList(outputTag));
        this.type = TagType.RULE;
        this.value = this.toString();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Rule rule = (Rule) o;

        if (inputFacts != null ? !inputFacts.equals(rule.inputFacts) : rule.inputFacts != null) return false;
        return outputTags != null ? outputTags.equals(rule.outputTags) : rule.outputTags == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (inputFacts != null ? inputFacts.hashCode() : 0);
        result = 31 * result + (outputTags != null ? outputTags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{ " + inputFacts + "=>" + outputTags + confidenceValue*100 +"% }";
    }

}
