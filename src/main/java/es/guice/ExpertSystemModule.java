package es.guice;

import com.google.inject.AbstractModule;
import es.internal.ExpertSystemInternalModule;

public class ExpertSystemModule extends AbstractModule {
    protected void configure() {
        install(new ExpertSystemInternalModule());
    }

    /**
     * Ensure that multiple inclusions of this module are collapsed
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj != null) && obj.getClass().equals(getClass());
    }

    /**
     * Ensure that multiple inclusions of this module are collapsed
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
