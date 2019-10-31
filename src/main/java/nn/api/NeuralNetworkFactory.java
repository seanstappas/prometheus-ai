package nn.api;

import com.google.inject.Inject;

/**
 * Guice factory for the NN.
 */
public interface NeuralNetworkFactory {
    /**
     * Creates the NN.
     *
     * @return the created NN.
     */
    @Inject
    NeuralNetwork create();
}
