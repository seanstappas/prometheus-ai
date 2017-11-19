package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

class ForwardSearcher extends Searcher<Set<Tag>> {
    private final DirectSearcher directSearcher;

    @Inject
    public ForwardSearcher(
            @Assisted DirectSearcher directSearcher) {
        this.directSearcher = directSearcher;
    }

    @Override
    public Set<Tag> searchInternal(Set<Tag> inputTags, double ply) {
        Set<Tag> allActivatedTags = new HashSet<>(inputTags);
        for (int i = 0; i < ply && !inputTags.isEmpty(); i++) {
            Set<Tag> activatedTags = new HashSet<>();
            for (Tag t : inputTags) {
                Set<Tag> directActivatedTags = directSearcher.search(t);
                activatedTags.addAll(directActivatedTags);
            }
            allActivatedTags.addAll(activatedTags);
            inputTags = activatedTags;
        }
        return allActivatedTags;
    }
}
