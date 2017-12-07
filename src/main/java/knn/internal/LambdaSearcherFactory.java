package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Guice factory for the lambda searcher.
 */
interface LambdaSearcherFactory {
    /**
     * Creates the lambda searcher.
     *
     * @param forwardSearcher  the forward searcher
     * @param backwardSearcher the backward searcher
     * @return the created lambda searcher
     */
    @Inject
    LambdaSearcher create(
            @Assisted ForwardSearcher forwardSearcher,
            @Assisted BackwardSearcher backwardSearcher);
}
