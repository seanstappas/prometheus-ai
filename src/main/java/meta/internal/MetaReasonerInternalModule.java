package meta.internal;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import meta.api.MetaReasoner;
import meta.api.MetaReasonerFactory;

public class MetaReasonerInternalModule extends AbstractModule {
  protected void configure() {
    install(new FactoryModuleBuilder()
        .implement(MetaReasoner.class, MetaReasonerImpl.class)
        .build(MetaReasonerFactory.class));
  }
}
