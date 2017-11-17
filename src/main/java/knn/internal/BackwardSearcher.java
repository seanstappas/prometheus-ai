package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.Map;
import java.util.Set;

public class BackwardSearcher extends Searcher<Set<Tag>> {
    private final Map<Tag, KnowledgeNode> mapKN;
    private final Set<Tag> activeTags;
    private final double partialMatchRatio;

    @Inject
    public BackwardSearcher(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("partialMatchRatio") double partialMatchRatio) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
        this.partialMatchRatio = partialMatchRatio;
    }

    @Override
    public Set<Tag> search(Set<Tag> input, int ply) {
        return null;
    }
}
