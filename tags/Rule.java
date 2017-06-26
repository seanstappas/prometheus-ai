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

    /**
     * Creates a Rule.
     *
     * @param inputFacts condition facts of the rule
     * @param outputTag  outputTags tag of the rule
     */
    public Rule(Fact[] inputFacts, Fact[] outputTag) {
        this.inputFacts = inputFacts;
        this.outputTags = outputTag;
        this.type = TagType.RULE;
        this.value = this.toString();
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
    public Rule(String[] inputFacts, String[] outputTags, TagType type) {
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
    }

    /**
     * Creates a Rule from a single String.
     * <p>
     * Example of a rule string:
     * <p>
     * "P1(ARG1,ARG2) P2(ARG3) -> @P3(ARG4,ARG5,ARG6)"
     * <p>
     * NB: 1. Tags are separated by " " 2. arguments with a fact tag are separated by ","
     * 3. all facts on the left of "->" are input facts, on the right are output tags
     * 4. if an output tag is preceeded by "@", it is a recommendation, otherwise it is a fact
     *
     * @param value the String representing the Rule
     */

    public Rule(String value) {
        String[] tokens = value.split(" ");
        ArrayList<Fact> inputFactList = new ArrayList<Fact>();
        ArrayList<Fact> outputTagList = new ArrayList<Fact>();
        String delim = "->";
        Boolean isOutputTag = false;
        for (String token : tokens) {
            if (token.equals(delim)) {
                isOutputTag = true;
            } else if (!token.contains("@")) {
                Fact fact = new Fact(token);
                if (!isOutputTag) {
                    inputFactList.add(fact);
                } else {
                    outputTagList.add(fact);
                }
            } else {
                Recommendation rec = new Recommendation(token);
                outputTagList.add(rec);
            }
        }
        Fact[] inputFacts = inputFactList.toArray(new Fact[inputFactList.size()]);
        Fact[] outputTag = outputTagList.toArray(new Fact[outputTagList.size()]);

        this.inputFacts = inputFacts;
        this.outputTags = outputTag;
        this.type = TagType.RULE;
        this.value = this.toString();
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
        return Arrays.toString(inputFacts) + "=>" + Arrays.toString(outputTags);
    }
}
