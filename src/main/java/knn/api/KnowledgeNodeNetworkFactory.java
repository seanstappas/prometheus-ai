package knn.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Tag;

import java.util.Map;

public interface KnowledgeNodeNetworkFactory {
    @Inject
    KnowledgeNodeNetwork create(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("inputTags") Map<Tag, Double> inputTags,
            @Assisted("activeTags") Map<Tag, Double> activeTags);
}
