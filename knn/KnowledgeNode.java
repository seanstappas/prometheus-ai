package knn;

import tags.Tag;

public class KnowledgeNode { // (inputTag -> outputTags)
    Tag inputTag;
    int activation = 0; // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    // Is threshold the same for all nodes? No
    int threshold = 1; // limit: When activation > threshold : fires output tags (outputTags array). These tags can be lists of rules or facts.
    int age = 0; // When a node is newly formed it has an age of zero.
    // When the node’s age increases to a value greater than or equal to K the node is then deleted.
    // The age parameter ages in a particular way.  It ages only if it is not used.  Every time a node is used the age is reset to zero.
    // If the node is not used after an “tau” amount of time it will age.
    // Ages linearly or using sigmoid.
    int strength; // How is strength used? Read doc...
    int confidence; // How is confidence used? Read doc...
    Tag[] outputTags; // What is this for? These are the output tags, fired when activation > threshold.

    public KnowledgeNode(Tag inputTag, Tag[] outputTags) {
        this.inputTag = inputTag;
        this.outputTags = outputTags;
    }

    public KnowledgeNode(String inputTag, String[] outputTags) {
        this.inputTag = new Tag(inputTag, Tag.Type.FACT);
        int n = outputTags.length;
        this.outputTags = new Tag[n];
        for (int i = 0; i < n; i++) {
            this.outputTags[i] = new Tag(outputTags[i], Tag.Type.FACT);
        }
    }

    // Should age increment at every think() cycle? Or independently, after some amount of time? System will have (daily) timestamp, nodes will have timestamp updated at every firing. Look at difference between the two before deciding to fire
    public void age() {
        age++;
    }
}