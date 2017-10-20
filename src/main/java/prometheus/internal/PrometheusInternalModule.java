package prometheus.internal;

import com.google.inject.AbstractModule;
import prometheus.api.Prometheus;

public class PrometheusInternalModule extends AbstractModule {
    protected void configure() {
        bind(Prometheus.class).to(PrometheusImpl.class);
    }
}
