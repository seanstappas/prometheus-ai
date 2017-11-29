package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BackwardSearcher extends Searcher<Set<Tag>> {
    private final Map<Tag, KnowledgeNode> mapKN;
    private final Set<Tag> activeTags;
    private final BackwardSearchMatcher backwardSearchMatcher;
    private double partialMatchRatio;

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

    void setMatchRatio(double partialMatchRatio) {
        this.partialMatchRatio = partialMatchRatio;
    }

    @Override
    public Set<Tag> searchInternal(Set<Tag> inputTags, double ply) {
        Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> currentPlyInputTags = new HashSet<>(inputTags);
        for (int i = 0; i < ply && !currentPlyInputTags.isEmpty(); i++) {
            int numRequiredMatches = (int)(partialMatchRatio * currentPlyInputTags.size());
            Set<Tag> activatedTags = new HashSet<>();
            for (KnowledgeNode kn : mapKN.values()) {
                backwardSearchMatcher.match(currentPlyInputTags, kn, numRequiredMatches).ifPresent(activatedTags::add);
            }
            allActivatedTags.addAll(activatedTags);
            currentPlyInputTags = activatedTags;
        }
        allActivatedTags.removeIf(this.activeTags::contains);
        this.activeTags.addAll(allActivatedTags);
        return allActivatedTags;
    }
}
