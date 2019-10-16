package prometheus.guice;

import com.google.inject.AbstractModule;
import knn.guice.KnowledgeNodeNetworkModule;
import prometheus.internal.PrometheusInternalModule;

/**
 * Public Guice module for Prometheus.
 */
public final class PrometheusModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new KnowledgeNodeNetworkModule());

        install(new PrometheusInternalModule());
    }
}
