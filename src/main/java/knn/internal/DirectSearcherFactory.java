package knn.internal;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

/**
 * Guice factory for the direct searcher.
 */
interface DirectSearcherFactory {
    /**
     * Creates the direct searcher.
     *
     * @param mapKN        the mapping from tags to KNs
     * @param activeTags   the active Tags
     * @param ageSortedKNs the KNs sorted by age
     * @return the created direct searcher
     */
    @Inject
    DirectSearcher create(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs);
}
