package prometheus.internal;

import es.api.ExpertSystem;
import es.api.ExpertSystemFactory;
import knn.api.KnowledgeNodeNetwork;
import prometheus.api.Prometheus;

import javax.inject.Inject;
import java.util.HashSet;

class PrometheusImpl implements Prometheus{
    private ExpertSystem es;
    private KnowledgeNodeNetwork knn;

    @Inject
    public PrometheusImpl(ExpertSystemFactory expertSystemFactory, KnowledgeNodeNetwork knn) {
        this.es = expertSystemFactory.create(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        this.knn = knn;
    }

    @Override
    public ExpertSystem getExpertSystem() {
        return es;
    }

    @Override
    public KnowledgeNodeNetwork getKnowledgeNodeNetwork() {
        return knn;
    }
}
