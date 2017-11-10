package knn.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashMap;
import java.util.Map;

public final class KnowledgeNode {
    public Tag inputTag;
    public Map<Tag, Double> outputs;                                        // Integer is the value of confidence
    public double activation = 0;                                                    // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    public double threshold;                                                        // limit: When activation > threshold : fires output tags (outputFacts array). These tags can be lists of rules or facts.
    public double belief = 0;
    public Map<Tag, Double> listOfRelatedTruth;
    public int strength = 1;                                                    // Which strength approach to take?
    public boolean isActivated = false;
    public boolean isFired = false;
    public double[] accuracy = {0, 2, 5, 11, 27, 50, 73, 88, 95, 98, 100};        //sigmoid function activation value
    private double age = System.currentTimeMillis() / 1000L;                    // Age timestamp. Set to current UNIX time when node is newly formed.
    private double maxAge = 60;

    public KnowledgeNode(Tag inputTag, Map<Tag, Double> outputFacts, int threshold) {
        this.inputTag = inputTag;
        this.outputs = outputFacts;
        this.threshold = threshold;
        this.listOfRelatedTruth = new HashMap<>();
    }

    /**
     * Full constructor
     *
     * @param inputName   the input tag of the Knowledge Node
     * @param outputFacts the output Tag of the Knowledge Node
     * @param threshold   the threshold of activation
     * @param strength    the strength value to bias activation
     * @param maxAge      threshold age for the node to be discarded
     */
    public KnowledgeNode(Tag inputName, Map<Tag, Double> outputFacts, int threshold, int strength, double maxAge) {
        this.inputTag = inputName;
        this.outputs = outputFacts;
        this.threshold = threshold;
        this.outputs = outputFacts;
        this.threshold = threshold;
        this.strength = strength;
        this.maxAge = maxAge;
        this.listOfRelatedTruth = new HashMap<>();
    }

    /**
     * Creates a Knowledge Node from Strings.
     *
     * @param inputInfo The info String to create the Knowledge Node
     */
    public KnowledgeNode(String[] inputInfo) {
        this.listOfRelatedTruth = new HashMap<>();
        this.outputs = new HashMap<>();

        if (inputInfo[0].charAt(0) == '@') {
            this.inputTag = new Recommendation(inputInfo[0]);
        } else if (inputInfo[0].contains("->")) {
            this.inputTag = new Rule(inputInfo[0]);
        } else if (inputInfo[0].matches(".*\\(.*\\).*")) {
            this.inputTag = new Fact(inputInfo[0]);
        }
        this.threshold = Integer.parseInt(inputInfo[1]);

        for (int i = 2; i < inputInfo.length; i += 2) {
            if (inputInfo[i].charAt(0) == '@') {
                Recommendation rcmd = new Recommendation(inputInfo[i]);
                this.outputs.put(rcmd, Double.parseDouble(inputInfo[i + 1]));
            } else if (inputInfo[i].contains("->")) {
                Rule r = new Rule(inputInfo[i]);
                this.outputs.put(r, Double.parseDouble(inputInfo[i + 1]));
            } else if (inputInfo[i].matches(".*\\(.*\\).*")) {
                Fact f = new Fact(inputInfo[i]);
                this.outputs.put(f, Double.parseDouble(inputInfo[i + 1]));
            }
        }
    }

    /**
     * update object confidence value
     */
    public void updateBelief() {
        double sum = 0;
        for (Tag t : this.listOfRelatedTruth.keySet()) {
            sum = sum + this.listOfRelatedTruth.get(t);
        }
        this.belief = sum / this.listOfRelatedTruth.size();
    }

    /**
     * Ages the current Knowledge Node.
     *
     * @return the age (time elapsed since initialisation/last update)
     */
    public double updateAge() {
        this.age = (System.currentTimeMillis() / 1000L) - this.age;
        return this.age;
    }

    public void setBelief(double belief) {
        this.belief = belief;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("inputTag", inputTag)
                .append("outputs", outputs)
                .append("activation", activation)
                .append("threshold", threshold)
                .append("belief", belief)
                .append("listOfRelatedTruth", listOfRelatedTruth)
                .append("strength", strength)
                .append("isActivated", isActivated)
                .append("isFired", isFired)
                .append("accuracy", accuracy)
                .append("age", age)
                .append("maxAge", maxAge)
                .toString();
    }

    public void increaseActivation() {
        this.activation++;
    }

    public void increaseActivation(int value) {
        this.activation = this.activation + this.accuracy[value];
    }

    public double getActivation() {
        return activation;
    }

}
