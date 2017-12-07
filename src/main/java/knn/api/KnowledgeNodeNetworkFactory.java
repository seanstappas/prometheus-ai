package knn.api;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

/**
 * Guice factory to create a KNN.
 */
public interface KnowledgeNodeNetworkFactory {
    /**
     * Creates a KNN.
     *
     * @param mapKN                    a mapping from input Tags to KNs
     * @param activeTags               the active Tags
     * @param ageSortedKNs             the KNs sorted by age
     * @param backwardSearchMatchRatio the backward search matching ratio
     * @param backwardSearchAgeLimit   the backward search age limit
     * @return the created KNN
     */
    @Inject
    KnowledgeNodeNetwork create(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs,
            @Assisted("backwardSearchMatchRatio")
                    double backwardSearchMatchRatio,
            @Assisted("backwardSearchAgeLimit") long backwardSearchAgeLimit);
}
