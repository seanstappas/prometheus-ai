package knn.internal;

import com.google.inject.AbstractModule;
import knn.api.KnowledgeNodeNetwork;

/**
 * Created by Sean on 10/13/2017.
 */
public class KnowledgeNodeNetworkInternalModule extends AbstractModule {
    protected void configure() {
        bind(KnowledgeNodeNetwork.class).to(KnowledgeNodeNetworkImpl.class);
    }

    /**
     * Ensure that multiple inclusions of this module are collapsed
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj != null) && obj.getClass().equals(getClass());
    }

    /**
     * Ensure that multiple inclusions of this module are collapsed
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
