package knn.internal;

import java.util.Set;
import java.util.TreeSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

/**
 * Guice factory for the backward searcher.
 */
interface BackwardSearcherFactory {
    /**
     * Creates the backward searcher.
     *
     * @param activeTags        the active tags
     * @param ageSortedKNs      the KNs sorted by age
     * @param partialMatchRatio the partial match ratio
     * @param ageLimit          the age limit
     * @return the created backward searcher
     */
    @Inject
    BackwardSearcher create(
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs,
            @Assisted("partialMatchRatio") double partialMatchRatio,
            @Assisted("ageLimit") long ageLimit);
}
