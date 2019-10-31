package es.guice;

import com.google.inject.AbstractModule;
import es.internal.ExpertSystemInternalModule;

/**
 * Guice module to load the ES.
 */
public final class ExpertSystemModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ExpertSystemInternalModule());
    }
}
