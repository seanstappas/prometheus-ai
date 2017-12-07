package meta.guice;

import com.google.inject.AbstractModule;
import meta.internal.MetaReasonerInternalModule;

/**
 * Public Guice module for the META.
 */
public final class MetaReasonerModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new MetaReasonerInternalModule());
    }
}
