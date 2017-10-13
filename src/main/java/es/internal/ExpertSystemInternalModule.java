package es.internal;

import com.google.inject.AbstractModule;
import es.api.ExpertSystem;

public class ExpertSystemInternalModule extends AbstractModule {
    protected void configure() {
        bind(ExpertSystem.class).to(ExpertSystemImpl.class);
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
