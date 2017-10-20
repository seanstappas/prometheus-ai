package prometheus.internal;

import es.api.ExpertSystem;
import es.api.ExpertSystemFactory;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeNetworkFactory;
import prometheus.api.Prometheus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;

class PrometheusImpl implements Prometheus{
    private ExpertSystem es;
    private KnowledgeNodeNetwork knn;

    @Inject
    public PrometheusImpl(ExpertSystemFactory expertSystemFactory, KnowledgeNodeNetworkFactory knowledgeNodeNetworkFactory) {
        this.es = expertSystemFactory.create(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        this.knn = knowledgeNodeNetworkFactory.create(new HashMap<>(), new HashMap<>(), new HashMap<>());
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
