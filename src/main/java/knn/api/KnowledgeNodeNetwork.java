package knn.api;

import tags.Tag;

import java.util.Collection;
import java.util.Set;

public interface KnowledgeNodeNetwork {
    /**
     * Resets the KNN to a state from a database.
     *
     * @param dbFilename the filename of the database to be read from
     */
    void reset(String dbFilename);

    /**
     * Resets the KNN by clearing all data structures.
     */
    void resetEmpty();

    /**
     * Saves the current state of the KNN to a database.
     *
     * @param dbFilename the filename of the database
     */
    void save(String dbFilename);

    /**
     * Clears all the Knowledge Nodes from the KNN.
     */
    void clearKnowledgeNodes();

    /**
     * Adds a Knowledge Node to the KNN.
     *
     * @param kn the Knowledge Node to be added
     */
    void addKnowledgeNode(KnowledgeNode kn);

    /**
     * Deletes a Knowledge Node from the KNN.
     *
     * @param tag the input Tag of the Knowledge Node to be deleted
     */
    void deleteKnowledgeNode(Tag tag);

    /**
     * Adds a fired Tag to the KNN.
     *
     * @param tag the fired Tag to be added
     */
    void addActiveTag(Tag tag);

    void addActiveTags(Tag... tags);

    /**
     * Get access of active Tags
     *
     * @return the Access of active Tags
     */
    Set<Tag> getActiveTags();

    KnowledgeNode getKnowledgeNode(Tag tag);

    Collection<KnowledgeNode> getKnowledgeNodes();

    // For all search methods, the Tags returned are the NEWLY fired tags, excluding the input tags.

    Set<Tag> directSearch(Tag inputTag);

    Set<Tag> forwardSearch(Set<Tag> inputTags, int ply);

    Set<Tag> forwardThink(int ply);

    Set<Tag> backwardSearch(Set<Tag> inputTags, int ply);

    Set<Tag> backwardThink(int ply);

    void setBackwardSearchMatchRatio(double ratio);

    Set<Tag> lambdaSearch(Set<Tag> inputTags, int ply);

    Set<Tag> lambdaThink(int ply);
}
