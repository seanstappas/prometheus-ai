package nn.api;

import com.google.inject.Inject;

public interface NeuralNetworkFactory {
  @Inject
  NeuralNetwork create();
}
