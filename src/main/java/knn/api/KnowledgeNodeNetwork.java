package knn.api;

import java.util.List;
import java.util.Set;
import tags.Tag;

/**
 * The Knowledge Node Network (KNN).
 */
public interface KnowledgeNodeNetwork {

    /**
     * Resets the KNN by clearing all data structures.
     */
    void resetEmpty();

    /**
     * Deactivates all the active Tags in the KNN.
     */
    void clearActiveTags();

    /**
     * Adds a Knowledge Node to the KNN.
     *
     * @param kn the Knowledge Node to be added
     */
    void addKnowledgeNode(KnowledgeNode kn);

    /**
     * Deletes expired KNs from the KNN, i.e., ones who have aged beyond their
     * age threshold.
     */
    void deleteExpiredKnowledgeNodes();

    /**
     * Deletes a Knowledge Node from the KNN.
     *
     * @param tag the input Tag of the Knowledge Node to be deleted
     */
    void deleteKnowledgeNode(Tag tag);

    /**
     * Adds an active Tag to the KNN.
     *
     * @param tag the active Tag to be added
     */
    void addActiveTag(Tag tag);

    /**
     * Adds multiple active Tags to the KNN.
     *
     * @param tags the Tags to be added
     */
    void addActiveTags(Tag... tags);

    /**
     * Gets the currently active Tags in the KNN.
     *
     * @return the currently active Tags
     */
    Set<Tag> getActiveTags();

    /**
     * Gets the KN associated with the given input Tag.
     *
     * @param tag the KN input Tag
     * @return the KN associated with the Tag
     */
    KnowledgeNode getKnowledgeNode(Tag tag);

    /**
     * Gets all the KNs in the KNN, sorted by age.
     *
     * @return all the KNs in the KNN
     */
    Set<KnowledgeNode> getKnowledgeNodes();


    /**
     * Performs direct search in the KNN.
     *
     * @param inputTag the input Tag of the search
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tag)
     */
    Set<Tag> directSearch(Tag inputTag);

    /**
     * Performs forward search in the KNN.
     *
     * @param inputTags the input Tags of the search
     * @param ply       the ply of the search. If set to 0, the search continues
     *                  until quiescence.
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tags)
     */
    Set<Tag> forwardSearch(Set<Tag> inputTags, int ply);

    /**
     * Performs forward thinking in the KNN. Note that this is equivalent to
     * forward searching with all the currently active Tags as input.
     *
     * @param ply the ply of the search. If set to 0, the search continues until
     *            quiescence.
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tags)
     */
    Set<Tag> forwardThink(int ply);

    /**
     * Performs backward search in the KNN.
     *
     * @param inputTags the input Tags of the search
     * @param ply       the ply of the search. If set to 0, the search continues
     *                  until quiescence.
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tags)
     */
    Set<Tag> backwardSearch(Set<Tag> inputTags, int ply);

    /**
     * Performs backward thinking in the KNN. Note that this is equivalent to
     * backward searching with all the currently active Tags as input.
     *
     * @param ply the ply of the search. If set to 0, the search continues until
     *            quiescence.
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tags)
     */
    Set<Tag> backwardThink(int ply);

    /**
     * Sets the search matching ratio for backward search, i.e., the ratio of
     * search input Tags that must be found in the output Tags of a KN to
     * activate its input Tag. See {@link knn.internal.BackwardSearchMatcher}
     * the implementation of backward search matching.
     *
     * @param ratio the backward search match ratio
     */
    void setBackwardSearchMatchRatio(double ratio);

    /**
     * Performs lambda search in the KNN.
     *
     * @param inputTags the input Tags of the search
     * @param ply       the ply of the search. If set to 0, the search continues
     *                  until quiescence.
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tags)
     */
    Set<Tag> lambdaSearch(Set<Tag> inputTags, int ply);

    /**
     * Performs lambda thinking in the KNN. Note that this is equivalent to
     * lambda searching with all the currently active Tags as input.
     *
     * @param ply the ply of the search. If set to 0, the search continues until
     *            quiescence.
     * @return the Set of activated Tags resulting from searching (excluding the
     * input Tags)
     */
    Set<Tag> lambdaThink(int ply);

    /**
     * Loads the data in the file with the given name into the KNN.
     *
     * @param filename the name of the file with the KNN data to load
     * @return a List of Knowledge Nodes extracted from the data file
     */
    List<KnowledgeNode> loadData(String filename);

    /**
     * Resets the KNN to a state from a database.
     *
     * @param dbFilename the filename of the database to be read from
     */
    void reset(String dbFilename);

    /**
     * Saves the current state of the KNN to a database.
     *
     * @param dbFilename the filename of the database
     */
    void save(String dbFilename);
}
