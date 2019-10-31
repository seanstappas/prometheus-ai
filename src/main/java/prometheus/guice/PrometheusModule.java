package prometheus.guice;

import com.google.inject.AbstractModule;
import es.guice.ExpertSystemModule;
import knn.guice.KnowledgeNodeNetworkModule;
import meta.guice.MetaReasonerModule;
import nn.guice.NeuralNetworkModule;
import prometheus.internal.PrometheusInternalModule;

/**
 * Public Guice module for Prometheus.
 */
public final class PrometheusModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new NeuralNetworkModule());
        install(new KnowledgeNodeNetworkModule());
        install(new ExpertSystemModule());
        install(new MetaReasonerModule());

        install(new PrometheusInternalModule());
    }
}
