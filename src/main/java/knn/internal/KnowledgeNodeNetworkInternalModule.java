package knn.internal;

import com.google.inject.AbstractModule;
import knn.api.KnowledgeNodeNetwork;

public class KnowledgeNodeNetworkInternalModule extends AbstractModule {
    protected void configure() {
        bind(KnowledgeNodeNetwork.class).to(KnowledgeNodeNetworkImpl.class);
    }
}
