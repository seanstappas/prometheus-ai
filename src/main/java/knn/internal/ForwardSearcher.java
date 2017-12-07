package knn.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

/**
 * Searcher which performs forward search in the KNN.
 */
class ForwardSearcher extends Searcher<Set<Tag>> {
    private final DirectSearcher directSearcher;

    @Inject
    ForwardSearcher(
            @Assisted final DirectSearcher directSearcher) {
        this.directSearcher = directSearcher;
    }

    @Override
    Set<Tag> searchInternal(final Set<Tag> inputTags, final double ply) {
        final Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> currentPlyInputTags = new HashSet<>(inputTags);
        for (int i = 0; i < ply && !currentPlyInputTags.isEmpty(); i++) {
            final Set<Tag> activatedTags = new HashSet<>();
            for (final Tag t : currentPlyInputTags) {
                final Set<Tag> directActivatedTags = directSearcher.search(t);
                activatedTags.addAll(directActivatedTags);
            }
            allActivatedTags.addAll(activatedTags);
            currentPlyInputTags = activatedTags;
        }
        return Collections.unmodifiableSet(allActivatedTags);
    }
}
