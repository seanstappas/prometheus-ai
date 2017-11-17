package knn.internal;

import tags.Tag;

import java.util.Set;

abstract class Searcher<T> {
    abstract Set<Tag> search(T input, int ply);

    Set<Tag> search(T input) {
        return search(input, Integer.MAX_VALUE);
    }
}
