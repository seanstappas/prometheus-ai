package prometheus.internal;

import com.google.inject.AbstractModule;
import prometheus.api.Prometheus;

/**
 * Internal Guice module for Prometheus.
 */
public final class PrometheusInternalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Prometheus.class).to(PrometheusImpl.class);
    }
}
