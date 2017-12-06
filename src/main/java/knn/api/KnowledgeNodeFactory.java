package knn.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public interface KnowledgeNodeFactory {
    @Inject
    KnowledgeNode create(
            @Assisted("data") String[] data);
}
