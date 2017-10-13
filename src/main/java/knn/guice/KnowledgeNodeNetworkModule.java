package knn.guice;

import com.google.inject.AbstractModule;
import knn.internal.KnowledgeNodeNetworkInternalModule;

/**
 * Created by Sean on 10/13/2017.
 */
public class KnowledgeNodeNetworkModule extends AbstractModule {
    protected void configure() {
        install(new KnowledgeNodeNetworkInternalModule());
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
