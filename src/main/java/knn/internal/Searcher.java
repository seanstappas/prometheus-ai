package knn.internal;

import java.util.Set;
import tags.Tag;

/**
 * Interface for cascading searchers (forward, backward and lambda).
 *
 * @param <T> the type of the search input
 */
abstract class Searcher<T> {
    /**
     * Internal search method.
     *
     * @param input the search input
     * @param ply   the ply of the search
     * @return the Tags activated as a result of searching
     */
    abstract Set<Tag> searchInternal(T input, double ply);

    /**
     * Public searcher.
     *
     * @param input the search input
     * @param ply   the ply of the search
     * @return the Tags activated as a result of searching
     */
    Set<Tag> search(final T input, final double ply) {
        if (ply == 0) {
            return searchInternal(input, Double.POSITIVE_INFINITY);
        }
        return searchInternal(input, ply);
    }
}
