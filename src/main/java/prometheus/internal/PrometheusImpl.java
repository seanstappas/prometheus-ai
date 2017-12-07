package prometheus.internal;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import es.api.ExpertSystem;
import es.api.ExpertSystemFactory;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeNetworkFactory;
import meta.api.MetaReasoner;
import meta.api.MetaReasonerFactory;
import nn.api.NeuralNetwork;
import nn.api.NeuralNetworkFactory;
import prometheus.api.Prometheus;

/**
 * Implementation of Prometheus AI.
 */
class PrometheusImpl implements Prometheus {
    private final NeuralNetwork nn;
    private final KnowledgeNodeNetwork knn;
    private final ExpertSystem es;
    private final MetaReasoner meta;

    @Inject
    PrometheusImpl(
            final NeuralNetworkFactory neuralNetworkFactory,
            final ExpertSystemFactory expertSystemFactory,
            final KnowledgeNodeNetworkFactory knowledgeNodeNetworkFactory,
            final MetaReasonerFactory metaReasonerFactory) {
        this.nn = neuralNetworkFactory.create();
        this.es = expertSystemFactory
                .create(new HashSet<>(), new HashSet<>(), new HashSet<>(),
                        new HashSet<>());
        this.knn = knowledgeNodeNetworkFactory.create(
                new HashMap<>(), new HashSet<>(), new TreeSet<>(), 1,
                Long.MAX_VALUE);
        this.meta = metaReasonerFactory.create();
    }

    @Override
    public NeuralNetwork getNeuralNetwork() {
        return nn;
    }

    @Override
    public KnowledgeNodeNetwork getKnowledgeNodeNetwork() {
        return knn;
    }

    @Override
    public ExpertSystem getExpertSystem() {
        return es;
    }

    @Override
    public MetaReasoner getMetaReasoner() {
        return meta;
    }
}
