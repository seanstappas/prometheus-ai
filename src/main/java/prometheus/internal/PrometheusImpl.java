package prometheus.internal;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeNetworkFactory;
import prometheus.api.Prometheus;

/**
 * Implementation of Prometheus AI.
 */
class PrometheusImpl implements Prometheus {
    private final KnowledgeNodeNetwork knn;

    @Inject
    PrometheusImpl(
            final KnowledgeNodeNetworkFactory knowledgeNodeNetworkFactory) {

        this.knn = knowledgeNodeNetworkFactory.create(
                new HashMap<>(), new HashSet<>(), new TreeSet<>(), 1,
                Long.MAX_VALUE);
    }


    @Override
    public KnowledgeNodeNetwork getKnowledgeNodeNetwork() {
        return knn;
    }
}
