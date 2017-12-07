package knn.internal;

import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

/**
 * Searcher which performs lambda search in the KNN.
 */
class LambdaSearcher extends Searcher<Set<Tag>> {
    private final ForwardSearcher forwardSearcher;
    private final BackwardSearcher backwardSearcher;

    @Inject
    LambdaSearcher(
            @Assisted final ForwardSearcher forwardSearcher,
            @Assisted final BackwardSearcher backwardSearcher) {
        this.forwardSearcher = forwardSearcher;
        this.backwardSearcher = backwardSearcher;
    }

    @Override
    Set<Tag> searchInternal(final Set<Tag> inputTags, final double ply) {
        final Set<Tag> backwardTags = backwardSearcher.search(inputTags, ply);
        return forwardSearcher.search(backwardTags, ply);
    }
}
