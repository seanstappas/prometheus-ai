package knn.api;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

/**
 * The Knowledge Node.
 */
public final class KnowledgeNode implements Comparable<KnowledgeNode> {
    private static final long AGE_THRESHOLD = 1_000_000;
    private static final int ACTIVATION_INCREMENT = 100;
    private static final int DEFAULT_THRESHOLD = 100;
    private static final int DEFAULT_BELIEF = 0;
    private static final int DEFAULT_STRENGTH = 1;

    private final Tag inputTag;
    private final Set<Tag> outputTags;
    private final int threshold;
    private final double belief;
    private final int strength;

    /**
     * Age timestamp. Set to current UNIX time when node is newly formed.
     */
    private long age = 0;
    private long initialAgeTimeStamp = System.currentTimeMillis();
    private double activation = 0;
    /**
     * true when the KN has exceeded its age threshold.
     */
    private boolean isExpired = false;

    /**
     * Creates a Knowledge Node from Strings.
     *
     * @param data The info String to create the Knowledge Node
     * @throws KnowledgeNodeParseException if parsing the given String data
     *                                     fails
     */
    @Inject
    public KnowledgeNode(
            @Assisted("data") final String[] data)
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

        int startOutputIndex = 1;
        if (data.length > 1 && StringUtils.isNumeric(data[1])) {
            this.threshold = Integer.parseInt(data[1]);
            startOutputIndex++;
        } else {
            this.threshold = DEFAULT_THRESHOLD;
        }

        for (int i = startOutputIndex; i < data.length; i += startOutputIndex) {
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
        this.belief = DEFAULT_BELIEF;
        this.strength = DEFAULT_STRENGTH;
    }

    public KnowledgeNode(final String data) throws KnowledgeNodeParseException {
        this(data.split(";\\s+"));
    }

    public KnowledgeNode(
            final Tag inputTag,
            final Set<Tag> outputTags,
            final int threshold) {
        this.inputTag = inputTag;
        this.outputTags = outputTags;
        this.threshold = threshold;
        this.belief = DEFAULT_BELIEF;
        this.strength = DEFAULT_STRENGTH;
    }

    public KnowledgeNode(
            final Tag inputTag,
            final Set<Tag> outputTags,
            final int threshold,
            final double belief,
            final int strength) {
        this.inputTag = inputTag;
        this.outputTags = outputTags;
        this.threshold = threshold;
        this.belief = belief;
        this.strength = strength;
    }

    /**
     * @return the current age of the KN
     */
    public long getCurrentAge() {
        return System.currentTimeMillis() - initialAgeTimeStamp;
    }

    /**
     * @return the belief associated with the KN
     */
    public double getBelief() {
        return belief;
    }

    /**
     * @return true if the KN has been newly fired, i.e., it was not fired
     * before this excitation
     */
    public boolean excite() {
        if (age > AGE_THRESHOLD) {
            isExpired = true;
            return false;
        } else {
            updateAge();
            final double oldActivation = activation;
            activation += ACTIVATION_INCREMENT;
            return oldActivation < threshold && isFired();
        }
    }

    /**
     * Ages the current Knowledge Node.
     */
    private void updateAge() {
        age = System.currentTimeMillis() - initialAgeTimeStamp;
        initialAgeTimeStamp = System.currentTimeMillis();
    }

    /**
     * @return true if the KN is fired
     */
    public boolean isFired() {
        return activation >= threshold;
    }

    /**
     * @return the input Tag of the KN
     */
    public Tag getInputTag() {
        return inputTag;
    }

    /**
     * @return the output Tags of the KN
     */
    public Set<Tag> getOutputTags() {
        return Collections.unmodifiableSet(outputTags);
    }

    /**
     * @return true if the KN is expired
     */
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final KnowledgeNode that = (KnowledgeNode) o;

        return new EqualsBuilder()
                .append(threshold, that.threshold)
                .append(strength, that.strength)
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
                .append(age)
                .append(belief)
                .append(activation)
                .toHashCode();
    }

    @Override
    public int compareTo(final KnowledgeNode o) {
        return new CompareToBuilder()
                .append(this.age, o.age)
                .append(this.hashCode(), o.hashCode())
                .toComparison();

    }
}
