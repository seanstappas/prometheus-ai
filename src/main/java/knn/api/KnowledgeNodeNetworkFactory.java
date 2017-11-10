package knn.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

import java.util.Map;
import java.util.Set;

public interface KnowledgeNodeNetworkFactory {
    @Inject
    KnowledgeNodeNetwork create(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags);
}
