package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectSearcher {
    private final Map<Tag, KnowledgeNode> mapKN;
    private final Set<Tag> activeTags;

    @Inject
    public DirectSearcher(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
    }

    public Set<Tag> search(Tag inputTag) {
        Set<Tag> activatedTags = new HashSet<>();
        if (mapKN.containsKey(inputTag)) {
            this.activeTags.add(inputTag);
            KnowledgeNode kn = mapKN.get(inputTag);
            boolean fired = kn.excite();
            if (fired) {
                activatedTags.addAll(kn.getOutputTags());
            }
        }
        this.activeTags.addAll(activatedTags);
        return activatedTags; // return activated OUTPUT tags (excluding the provided input)
    }
}
