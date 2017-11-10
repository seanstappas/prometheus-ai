package knn.api;

import tags.Tag;

import java.util.List;
import java.util.Set;

/**
 * Created by Sean on 10/13/2017.
 */
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
    void saveKNN(String dbFilename);

    /**
     * Clears all the Knowledge Nodes from the KNN.
     */
    void clearKN();

    /**
     * Adds a Knowledge Node to the KNN.
     *
     * @param kn the Knowledge Node to be added
     */
    void addKN(KnowledgeNode kn);

    /**
     * Deletes a Knowledge Node from the KNN.
     *
     * @param tag the input Tag of the Knowledge Node to be deleted
     */
    void delKN(Tag tag);

    /**
     * Adds a fired Tag to the KNN.
     *
     * @param tag the fired Tag to be added
     */
    void addFiredTag(Tag tag);

    /**
     * Get access of active Tags
     *
     * @return	the Access of active Tags
     */
    Set<Tag> getActiveTags();

    KnowledgeNode getKnowledgeNode(Tag tag);

    /**
     * Lambda search, a search to find out the best relation between a know list of tags and a wanted item tag
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param item the wanted item tag
     */
    void lambdaSearch(List<Tuple> NNoutputs, Tag item);

    /**
     * Backward searching with unlimited time
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param score the minimum number of matching needed from the output list of a KN in order for that KN to become active.
     */
    void backwardSearch(List<Tuple> NNoutputs, double score);

    /**
     * Creating input Tags from string in the output of Neural Network (NN)
     * This method is used only for backward or lambda search because no excitation is needed during the Tag creation
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     */
    void getInputForBackwardSearch(List<Tuple> NNoutputs);

    /**
     * Backward search with ply as input
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param score indication of accuracy
     * @param ply number of cycle the AI wanted to search
     */
    void backwardSearch(List<Tuple> NNoutputs, double score, int ply);

    /**
     * Forward searching with ply as number of depth
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param ply number of time of searching in the knowledge node network
     */
    void forwardSearch(List<Tuple> NNoutputs, int ply);

    /**
     * forwardSearch with unlimited time
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     */
    void forwardSearch(List<Tuple> NNoutputs);

    /**
     * Creating input Tags from string in the output of Neural Network (NN)
     * This method is used only for forward search because excitation may be active during the Tag creation
     *
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     */
    void getInputForForwardSearch(List<Tuple> NNoutputs);

    /**
     * Excites a Knowledge Node.
     *
     * @param kn the Knowledge Node to excite
     * @param value the accuracy from the neural network
     * If excitation leads to firing, this will add the fired kn to the activeTag.
     */
    void excite(KnowledgeNode kn, int value);

    /**
     * Fires a Knowledge Node.
     *
     * @param kn Knowledge Node to fire
     */
    void fire(KnowledgeNode kn);

    /**
     * Update the confidence of those active KN found in output list of a KN with its latest confidence value
     *
     * @param kn the kn that has a new confidence value
     */
    void updateConfidence(KnowledgeNode kn);
}
