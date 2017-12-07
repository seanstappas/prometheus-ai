package knn.internal;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import knn.api.KnowledgeNodeNetwork;
import knn.api.KnowledgeNodeNetworkFactory;

/**
 * The internal Guice module for the KNN.
 */
public final class KnowledgeNodeNetworkInternalModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(KnowledgeNodeNetwork.class,
                        KnowledgeNodeNetworkImpl.class)
                .build(KnowledgeNodeNetworkFactory.class));

        install(new FactoryModuleBuilder()
                .build(DirectSearcherFactory.class));
        install(new FactoryModuleBuilder()
                .build(ForwardSearcherFactory.class));
        install(new FactoryModuleBuilder()
                .build(BackwardSearcherFactory.class));
        install(new FactoryModuleBuilder()
                .build(LambdaSearcherFactory.class));
    }
}
