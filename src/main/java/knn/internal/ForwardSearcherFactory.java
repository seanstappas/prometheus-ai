package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Guice factory for the forward searcher.
 */
interface ForwardSearcherFactory {
    /**
     * Creates a forward searcher.
     *
     * @param directSearcher the direct searcher
     * @return the created forward searcher
     */
    @Inject
    ForwardSearcher create(
            @Assisted DirectSearcher directSearcher);
}
