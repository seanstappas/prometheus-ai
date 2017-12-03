package knn.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

public class KnowledgeNode {
    private static final int ACTIVATION_INCREMENT = 100;

    // Final fields
    private static final double[] SIGMOID_VALUES = {0, 2, 5, 11, 27, 50, 73, 88, 95, 98, 100}; //sigmoid function activation value
    private final Tag inputTag;
    private final Set<Tag> outputTags;  // Integer is the value of confidence
    private final int threshold; // limit: When activation > threshold : fires output tags (outputFacts array). These tags can be lists of rules or facts.
    private final int strength; // Which strength approach to take?
    private final double maxAge;

    // Modifiable fields
    private long age = 0; // Age timestamp. Set to current UNIX time when node is newly formed.
    private long ageThreshold = 1_000_000;
    private double belief = 0;
    private double activation = 0; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    private boolean isExpired = false; // true when the KN has exceeded its age threshold

    /**
     * Creates a Knowledge Node from Strings.
     *
     * @param data The info String to create the Knowledge Node
     */
    @Inject
    public KnowledgeNode(
            @Assisted("data") String[] data)
            throws KnowledgeNodeParseException {
        this.outputTags = new HashSet<>();

        if (data[0].charAt(0) == '@') {
            this.inputTag = new Recommendation(data[0]);
        } else if (data[0].contains("->")) {
            this.inputTag = new Rule(data[0]);
        } else if (data[0].matches(".*\\(.*\\).*")) {
            this.inputTag = new Fact(data[0]);
        } else {
            throw new KnowledgeNodeParseException(MessageFormat.format(
                    "Invalid input tag: {0}.", data[0]));
        }
        this.threshold = Integer.parseInt(data[1]);

        for (int i = 2; i < data.length; i += 2) {
            if (data[i].charAt(0) == '@') {
                this.outputTags.add(new Recommendation(data[i]));
            } else if (data[i].contains("->")) {
                this.outputTags.add(new Rule(data[i]));
            } else if (data[i].matches(".*\\(.*\\).*")) {
                this.outputTags.add(new Fact(data[i]));
            } else {
                throw new KnowledgeNodeParseException(MessageFormat.format(
                        "Invalid output tag: {0}.", data[i]));
            }
        }
        this.strength = 1;
        this.maxAge = 60;
    }

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
        this.inputTag.setAge(age);
        return this.age;
    }

    public double getBelief() {
        return belief;
    }

    public void setBelief(double belief) {
        this.belief = belief;
    }

    /**
     * @return true if the KN has been newly fired, i.e., it was not fired before this excitation.
     */
    public boolean excite() {
        if (age > ageThreshold) {
            isExpired = true;
            return false;
        } else {
            double oldActivation = activation;
            activation += ACTIVATION_INCREMENT;
            return oldActivation < threshold && isFired();
        }
    }

    public boolean isFired() {
        return activation >= threshold;
    }

    public double getActivation() {
        return activation;
    }


    public Tag getInputTag() {
        return inputTag;
    }


    public Set<Tag> getOutputTags() {
        return outputTags;
    }

    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("inputTag", inputTag)
                .append("outputTags", outputTags)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        KnowledgeNode that = (KnowledgeNode) o;

        return new EqualsBuilder()
                .append(threshold, that.threshold)
                .append(strength, that.strength)
                .append(maxAge, that.maxAge)
                .append(age, that.age)
                .append(belief, that.belief)
                .append(activation, that.activation)
                .append(inputTag, that.inputTag)
                .append(outputTags, that.outputTags)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(inputTag)
                .append(outputTags)
                .append(threshold)
                .append(strength)
                .append(maxAge)
                .append(age)
                .append(belief)
                .append(activation)
                .toHashCode();
    }
}
