package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Searcher which performs direct search in the KNN.
 */
class DirectSearcher {
    private final Map<Tag, KnowledgeNode> mapKN;
    private final Set<Tag> activeTags;

    @Inject
    public DirectSearcher(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
    }

    /**
     * Search for the given input Tag in the KN map.
     *
     * @param inputTag the input Tag to search for
     * @return the activated output tags (excluding the given input Tag)
     */
    Set<Tag> search(Tag inputTag) {
        Set<Tag> activatedTags = new HashSet<>();
        if (mapKN.containsKey(inputTag)) {
            KnowledgeNode kn = mapKN.get(inputTag);
            boolean fired = kn.excite();
            if (fired) {
                activatedTags.addAll(kn.getOutputTags());
            }
        }
        this.activeTags.add(inputTag);
        this.activeTags.addAll(activatedTags);
        return Collections.unmodifiableSet(activatedTags);
    }
}
