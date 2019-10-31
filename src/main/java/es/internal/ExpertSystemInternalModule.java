package es.internal;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import es.api.ExpertSystem;
import es.api.ExpertSystemFactory;

/**
 * Internal Guice module for the ES.
 */
public final class ExpertSystemInternalModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(ExpertSystem.class, ExpertSystemImpl.class)
                .build(ExpertSystemFactory.class));
        install(new FactoryModuleBuilder()
                .build(ThinkCycleExecutorFactory.class));
        install(new FactoryModuleBuilder()
                .build(ThinkerFactory.class));
        install(new FactoryModuleBuilder()
                .build(TeacherFactory.class));
        install(new FactoryModuleBuilder()
                .build(ResterFactory.class));
    }
}
