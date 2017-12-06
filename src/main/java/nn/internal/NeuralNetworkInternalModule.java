package nn.internal;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import nn.api.NeuralNetwork;
import nn.api.NeuralNetworkFactory;

public class NeuralNetworkInternalModule extends AbstractModule {
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(NeuralNetwork.class, NeuralNetworkImpl.class)
                .build(NeuralNetworkFactory.class));
    }
}
