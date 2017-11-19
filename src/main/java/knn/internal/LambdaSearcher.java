package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

import java.util.Set;

public class LambdaSearcher extends Searcher<Set<Tag>> {
    private final ForwardSearcher forwardSearcher;
    private final BackwardSearcher backwardSearcher;

    @Inject
    public LambdaSearcher(
            @Assisted ForwardSearcher forwardSearcher,
            @Assisted BackwardSearcher backwardSearcher) {
        this.forwardSearcher = forwardSearcher;
        this.backwardSearcher = backwardSearcher;
    }

    @Override
    public Set<Tag> search(Set<Tag> inputTags, int ply) {
        Set<Tag> backwardTags = backwardSearcher.search(inputTags, ply);
        return forwardSearcher.search(backwardTags, ply);
    }
}
