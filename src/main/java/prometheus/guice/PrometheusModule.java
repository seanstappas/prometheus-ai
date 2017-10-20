package prometheus.guice;

import com.google.inject.AbstractModule;
import es.guice.ExpertSystemModule;
import knn.guice.KnowledgeNodeNetworkModule;
import prometheus.internal.PrometheusInternalModule;

public class PrometheusModule extends AbstractModule {
    protected void configure() {
        install(new ExpertSystemModule());
        install(new KnowledgeNodeNetworkModule());
        install(new PrometheusInternalModule());
    }
}
