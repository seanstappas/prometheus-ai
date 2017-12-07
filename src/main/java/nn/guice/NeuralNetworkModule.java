package nn.guice;

import com.google.inject.AbstractModule;
import nn.internal.NeuralNetworkInternalModule;

/**
 * Public Guice module for the NN.
 */
public final class NeuralNetworkModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new NeuralNetworkInternalModule());
    }
}
