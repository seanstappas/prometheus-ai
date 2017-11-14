package graphing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import prometheus.guice.PrometheusModule;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GuiceDependencyGrapher {
    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new PrometheusModule());
        graph("prometheus.dot", injector);
    }

    private static void graph(String filename, Injector demoInjector) throws IOException {
        PrintWriter out = new PrintWriter(new File(filename), "UTF-8");
        Injector injector = Guice.createInjector(new GraphvizModule());
        GraphvizGrapher grapher = injector.getInstance(GraphvizGrapher.class);
        grapher.setOut(out);
        grapher.setRankdir("TB");
        grapher.graph(demoInjector);
    }
}
