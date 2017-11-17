package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.Map;
import java.util.Set;

interface BackwardSearcherFactory {
    @Inject
    BackwardSearcher create(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("partialMatchRatio") double partialMatchRatio);
}
