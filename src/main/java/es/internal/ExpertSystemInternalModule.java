package es.internal;

import com.google.inject.AbstractModule;
import es.api.ExpertSystem;

public class ExpertSystemInternalModule extends AbstractModule {
    protected void configure() {
        bind(ExpertSystem.class).to(ExpertSystemImpl.class);
    }
}
