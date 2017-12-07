package knn.guice;

import com.google.inject.AbstractModule;
import knn.internal.KnowledgeNodeNetworkInternalModule;

/**
 * Public Guice module for the KNN.
 */
public final class KnowledgeNodeNetworkModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new KnowledgeNodeNetworkInternalModule());
    }
}
