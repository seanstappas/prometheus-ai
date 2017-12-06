package es.guice;

import com.google.inject.AbstractModule;
import es.internal.ExpertSystemInternalModule;

public class ExpertSystemModule extends AbstractModule {
  protected void configure() {
    install(new ExpertSystemInternalModule());
  }
}
