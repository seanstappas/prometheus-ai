package nn.guice;

import com.google.inject.AbstractModule;
import nn.internal.NeuralNetworkInternalModule;

public class NeuralNetworkModule extends AbstractModule {
  protected void configure() {
    install(new NeuralNetworkInternalModule());
  }
}
