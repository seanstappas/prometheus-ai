package knn.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Guice factory to create a KN.
 */
public interface KnowledgeNodeFactory {
    /**
     * Creates a KN with the given Strings.
     *
     * @param data the String data to create the KN with
     * @return the created KN
     */
    @Inject
    KnowledgeNode create(
            @Assisted("data") String[] data);
}
