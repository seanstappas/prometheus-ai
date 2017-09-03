package tags;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a rule in the expert system. Rules are many-to-many structures with Facts as inputs and Facts and
 * Recommendations as outputs. They only activate when all the input Tags are active.
 */
public class Rule extends Tag {
    public Fact[] inputFacts;
    public Fact[] outputTags;

    public Fact[] getInputFacts() {
        return inputFacts;
    }

    public Fact[] getOutputTags() {
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
        this(inputFacts, outputTag, 1.0);
    }

    public Rule(Fact[] inputFacts, Fact[] outputTag, double confidenceValue) {
        this.inputFacts = inputFacts;
        this.outputTags = outputTag;
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
        int m = inputFacts.length;
        int n = outputTags.length;
        this.inputFacts = new Fact[m];
        this.outputTags = new Fact[n];
        for (int i = 0; i < m; i++) {
            this.inputFacts[i] = new Fact(inputFacts[i]);
        }
        for (int i = 0; i < n; i++) {
            this.outputTags[i] = (Fact) Tag.createTagFromString(outputTags[i], type);
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

    public static ArrayList<Rule> makeRules(String value) {
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(value.split(" ")));
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

        ArrayList<Rule> ruleList = new ArrayList<>();
        ArrayList<Fact> inputFactList = new ArrayList<>();

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
     * @deprecated use makeRule instead
     */

    public Rule(String string) {
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(string.split(" ")));
        int outputTagIndex = tokens.indexOf("->");

        ArrayList<Fact> outputTagList = new ArrayList<Fact>();
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
        this.inputFacts = inputFacts;
        this.outputTags = outputTag;
        this.type = TagType.RULE;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Rule rule = (Rule) o;
        return Arrays.equals(inputFacts, rule.inputFacts) && Arrays.equals(outputTags, rule.outputTags);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(inputFacts);
        result = 31 * result + Arrays.hashCode(outputTags);
        return result;
    }

    @Override
    public String toString() {
        return "{ " + Arrays.toString(inputFacts) + "=>" + Arrays.toString(outputTags) + confidenceValue*100 +"% }";
        }

    }
