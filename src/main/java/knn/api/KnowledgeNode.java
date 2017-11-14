package knn.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.commons.lang3.builder.ToStringBuilder;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashMap;
import java.util.Map;

public final class KnowledgeNode {
    // Final fields
    public static final double[] SIGMOID_VALUES = {0, 2, 5, 11, 27, 50, 73, 88, 95, 98, 100}; //sigmoid function activation value
    public final Tag inputTag;
    public final Map<Tag, Double> outputs;  // Integer is the value of confidence
    public final double threshold; // limit: When activation > threshold : fires output tags (outputFacts array). These tags can be lists of rules or facts.
    public final int strength; // Which strength approach to take?
    private final double maxAge;

    // Modifiable fields
    public boolean isActivated = false;
    public boolean isFired = false;
    public double age = 0; // Age timestamp. Set to current UNIX time when node is newly formed.
    public double belief = 0;
    public double activation = 0; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.

    /**
     * Creates a Knowledge Node from Strings.
     *
     * @param data The info String to create the Knowledge Node
     */
    @Inject
    public KnowledgeNode(
            @Assisted("data") String[] data)
            throws KnowledgeNodeParseException {
        this.outputs = new HashMap<>();

        if (data[0].charAt(0) == '@') {
            this.inputTag = new Recommendation(data[0]);
        } else if (data[0].contains("->")) {
            this.inputTag = new Rule(data[0]);
        } else if (data[0].matches(".*\\(.*\\).*")) {
            this.inputTag = new Fact(data[0]);
        } else {
            throw new KnowledgeNodeParseException("Invalid input tag.");
        }
        this.threshold = Integer.parseInt(data[1]);

        for (int i = 2; i < data.length; i += 2) {
            if (data[i].charAt(0) == '@') {
                Recommendation rcmd = new Recommendation(data[i]);
                this.outputs.put(rcmd, Double.parseDouble(data[i + 1]));
            } else if (data[i].contains("->")) {
                Rule r = new Rule(data[i]);
                this.outputs.put(r, Double.parseDouble(data[i + 1]));
            } else if (data[i].matches(".*\\(.*\\).*")) {
                Fact f = new Fact(data[i]);
                this.outputs.put(f, Double.parseDouble(data[i + 1]));
            }
        }
        this.strength = 1;
        this.maxAge = 60;
    }

    /**
     * update object confidence value
     */
    public void updateBelief() {
        // TODO: update belief correctly
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
                .append("strength", strength)
                .append("isActivated", isActivated)
                .append("isFired", isFired)
                .append("age", age)
                .append("maxAge", maxAge)
                .toString();
    }

    public void increaseActivation() {
        this.activation++;
    }

    public void increaseActivation(int value) {
        this.activation = this.activation + SIGMOID_VALUES[value];
    }

    public double getActivation() {
        return activation;
    }

}
