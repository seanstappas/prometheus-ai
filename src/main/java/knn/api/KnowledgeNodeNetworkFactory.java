package knn.api;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

public interface KnowledgeNodeNetworkFactory {
    @Inject
    KnowledgeNodeNetwork create(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs,
            @Assisted("backwardSearchMatchRatio")
                    double backwardSearchMatchRatio,
            @Assisted("backwardSearchAgeLimit") long backwardSearchAgeLimit);
}
