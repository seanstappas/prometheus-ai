package meta.guice;

import com.google.inject.AbstractModule;
import meta.internal.MetaReasonerInternalModule;

public class MetaReasonerModule extends AbstractModule {
  protected void configure() {
    install(new MetaReasonerInternalModule());
  }
}
