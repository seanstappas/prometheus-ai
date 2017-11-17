package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

public class ForwardSearcher extends Searcher<Set<Tag>> {
    private final DirectSearcher directSearcher;

    @Inject
    public ForwardSearcher(
            @Assisted DirectSearcher directSearcher) {
        this.directSearcher = directSearcher;
    }

    @Override
    public Set<Tag> search(Set<Tag> inputTags, int ply) {
        Set<Tag> allActivatedTags = new HashSet<>(inputTags);
        for (int i = 0; i < ply; i++) {
            Set<Tag> activatedTags = new HashSet<>();
            for (Tag t : inputTags) {
                Set<Tag> directActivatedTags = directSearcher.search(t);
                activatedTags.addAll(directActivatedTags);
            }
            allActivatedTags.addAll(activatedTags);
            if (activatedTags.isEmpty()) {
                break;
            }
            inputTags = activatedTags;
        }
        return allActivatedTags;
    }
}
