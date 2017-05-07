package knn;

import tags.Tag;

import java.util.Arrays;

/**
 * Represents a Knowledge Node in the Knowledge Node Network.
 */
public class KnowledgeNode { // (inputTag -> outputTags)
    Tag inputTag;
    Tag[] outputTags;
    int activation = 0; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    int threshold = 1; // limit: When activation > threshold : fires output tags (outputTags array). These tags can be lists of rules or facts.
    double age = System.currentTimeMillis() / 1000L; // Age timestamp. Set to current UNIX time when node is newly formed.
    int strength = 1; // TODO?: Which strength approach to take?
    int confidence = 100; // TODO: Implement confidence.
    private KNType type;
    double maxAge = 60;
    enum KNType {
        LINEAR,
        SIGMOID
    }

    /**
     * Simple constructor. By default: linear activation, strength = 1, and threshold = 1.
     *
     * @param inputTag    input tag
     * @param outputTags  output tags
     */
    public KnowledgeNode(Tag inputTag, Tag[] outputTags) { // TODO?: Which fields should be parameters to the constructor?
        this.inputTag = inputTag;
        this.outputTags = outputTags;
        this.type = KNType.LINEAR;
        this.threshold = 1;
        this.strength = 1;
        this.maxAge = 60;
    }

    /**
     * Full constructor
     *
     * @param inputTag    the input tag of the Knowledge Node
     * @param outputTags  the output Tag of the Knowledge Node
     * @param threshold   the threshold of activation
     * @param strength    the strength value to bias activation
     * @param type        the type of the Knowledge Node (linear or sigmoid activation)
     * @param maxAge      threshold age for the node to be discarded
     */
    public KnowledgeNode(Tag inputTag, Tag[] outputTags, int threshold, int strength, KNType type, double maxAge) {
        this.inputTag = inputTag;
        this.outputTags = outputTags;
        this.threshold = threshold;
        this.strength = strength;
        this.type = type;
        this.maxAge = maxAge;
    }

    /**
     * Creates a Knowledge Node from Strings. Assumes all Tags are of the provided TagType.
     *
     * @param inputTag    the input Tag of the Knowledge Node
     * @param outputTags  the output Tag of the Knowledge Node
     * @param tagType     the type of all Tags (input and output)
     */
    public KnowledgeNode(String inputTag, String[] outputTags, Tag.TagType tagType, KNType knType) {
        int n = outputTags.length;
        this.inputTag = Tag.createTagFromString(inputTag, tagType);
        this.outputTags = new Tag[n];
        for (int i = 0; i < n; i++) {
            this.outputTags[i] = Tag.createTagFromString(outputTags[i], tagType);
        }
        this.type = knType;
    }

    /**
     * Ages the current Knowledge Node.
     * @return the age (time elapsed since initialisation/last update)
     */

    public double updateAge() {
        this.age = (System.currentTimeMillis() / 1000L) - this.age;
        return this.age;
    }

    @Override
    public String toString() {
        return inputTag.toString() + "=>" + Arrays.toString(outputTags);
    }

    public void increaseActivation() {
        this.activation++;
    }

    public int getActivation() {
        if (type == KNType.LINEAR)
            return activation;
        else
            return fastSigmoidFunc(activation);
    }

    /**
     * Approximate sigmoid function.
     */
    public int fastSigmoidFunc(int x) {
        return x / (1 + Math.abs(x));
    }
}