package prometheus.api;

import es.api.ExpertSystem;
import knn.api.KnowledgeNodeNetwork;
import meta.api.MetaReasoner;
import nn.api.NeuralNetwork;

/**
 * Prometheus AI.
 */
public interface Prometheus {
    /**
     * @return the Neural Network (NN)
     */
    NeuralNetwork getNeuralNetwork();

    /**
     * @return the Knowledge Node Network (KNN)
     */
    KnowledgeNodeNetwork getKnowledgeNodeNetwork();

    /**
     * @return the Expert System (ES)
     */
    ExpertSystem getExpertSystem();

    /**
     * @return the Meta Reasoner (META)
     */
    MetaReasoner getMetaReasoner();
}
