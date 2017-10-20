package prometheus.api;

import es.api.ExpertSystem;
import knn.api.KnowledgeNodeNetwork;

public interface Prometheus {
    ExpertSystem getExpertSystem();

    KnowledgeNodeNetwork getKnowledgeNodeNetwork();
}
