package knn.internal;

import tags.Tag;

import java.util.Set;

abstract class Searcher<T> {
    abstract Set<Tag> searchInternal(T input, double ply);

    Set<Tag> search(T input, double ply) {
        if (ply == 0) {
            return searchInternal(input, Double.POSITIVE_INFINITY);
        }
        return searchInternal(input, ply);
    }
}