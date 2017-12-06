package knn.guice;

import com.google.inject.AbstractModule;
import knn.internal.KnowledgeNodeNetworkInternalModule;

public class KnowledgeNodeNetworkModule extends AbstractModule {
  protected void configure() {
    install(new KnowledgeNodeNetworkInternalModule());
  }
}
