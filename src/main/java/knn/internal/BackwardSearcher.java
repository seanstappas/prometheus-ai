package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BackwardSearcher extends Searcher<Set<Tag>> {
    private final Map<Tag, KnowledgeNode> mapKN;
    private final Set<Tag> activeTags;
    private final double partialMatchRatio;
    private final BackwardSearchMatcher backwardSearchMatcher;

    @Inject
    public BackwardSearcher(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("partialMatchRatio") double partialMatchRatio,
            BackwardSearchMatcher backwardSearchMatcher) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
        this.partialMatchRatio = partialMatchRatio;
        this.backwardSearchMatcher = backwardSearchMatcher;
    }

    @Override
    public Set<Tag> search(Set<Tag> inputTags, int ply) {
        int numRequiredMatches = (int)(partialMatchRatio * inputTags.size());
        Set<Tag> allActivatedTags = new HashSet<>(inputTags);
        for (int i = 0; i < ply; i++) {
            Set<Tag> activatedTags = new HashSet<>();
            for (KnowledgeNode kn : mapKN.values()) {
                activatedTags.addAll(backwardSearchMatcher.match(inputTags, kn, numRequiredMatches));
            }
            allActivatedTags.addAll(activatedTags);
            if (activatedTags.isEmpty()) {
                break;
            }
        }
        this.activeTags.addAll(allActivatedTags);
        return allActivatedTags;
    }
}
