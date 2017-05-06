package knn;

import tags.Tag;

import java.util.Arrays;

/**
 * Represents a Knowledge Node in the Knowledge Node Network.
 */
public class KnowledgeNode { // (inputTag -> outputTags)
    Tag inputTag;
    int activation = 0; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    int threshold = 1; // limit: When activation > threshold : fires output tags (outputTags array). These tags can be lists of rules or facts.
    double age = 0; // age = 0; // When a node is newly formed it has an age of zero.
    int strength = 1; // TODO?: Which strength approach to take?
    int confidence = 100; // TODO: Implement confidence.
    Tag[] outputTags;


    /**
     * Constructor
     *
     * @param inputTag    input tag
     * @param outputTags  output tags
     */
    public KnowledgeNode(Tag inputTag, Tag[] outputTags) { // TODO?: Which fields should be parameters to the constructor?
        this.inputTag = inputTag;
        this.outputTags = outputTags;
    }

    /**
     * Creates a Knowledge Node from Strings. Assumes all Tags are of the provided Type.
     *
     * @param inputTag    the input Tag of the Knowledge Node
     * @param outputTags  the output Tag of the Knowledge Node
     * @param type        the type of all Tags (input and output)
     */
    public KnowledgeNode(String inputTag, String[] outputTags, Tag.Type type) {
        int n = outputTags.length;
        this.inputTag = Tag.createTagFromString(inputTag, type);
        this.outputTags = new Tag[n];
        for (int i = 0; i < n; i++) {
            this.outputTags[i] = Tag.createTagFromString(outputTags[i], type);
        }
    }

    /**
     * Ages the current Knowledge Node.
     * @return the age (time elapsed since initialisation/last update)
     */

    public double updateAge() {
        this.age =  (System.currentTimeMillis() / 1000L) - this.age;
        return this.age;
    }

    @Override
    public String toString() {
        return inputTag.toString() + "=>" + Arrays.toString(outputTags);
    }
}