package prometheus.api;

import es.api.ExpertSystem;
import knn.api.KnowledgeNodeNetwork;
import meta.api.MetaReasoner;
import nn.api.NeuralNetwork;

public interface Prometheus {
  NeuralNetwork getNeuralNetwork();

  KnowledgeNodeNetwork getKnowledgeNodeNetwork();

  ExpertSystem getExpertSystem();

  MetaReasoner getMetaReasoner();
}
