package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.*;

/**
 * Searcher which performs direct search in the KNN.
 */
class DirectSearcher {
    private final Map<Tag, KnowledgeNode> mapKN;
    private final Set<Tag> activeTags;
    private final TreeSet<KnowledgeNode> ageSortedKNs;

    @Inject
    public DirectSearcher(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
        this.ageSortedKNs = ageSortedKNs;
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

            // Update age
            ageSortedKNs.remove(kn);
            kn.updateAge();
            ageSortedKNs.add(kn);
        }
        this.activeTags.add(inputTag);
        this.activeTags.addAll(activatedTags);
        return Collections.unmodifiableSet(activatedTags);
    }
}
