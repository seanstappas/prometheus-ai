package meta.api;

import com.google.inject.Inject;

/**
 * Guice factory to create the META.
 */
public interface MetaReasonerFactory {
    /**
     * Creates the META.
     *
     * @return the created META
     */
    @Inject
    MetaReasoner create();
}
