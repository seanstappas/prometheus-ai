package graphing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import prometheus.guice.PrometheusModule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GuiceDependencyVisualizer {
    private static final String GRAPH_SAVE_PATH = "graphs/guice_graph.dot";

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new PrometheusModule());
        graph(GRAPH_SAVE_PATH, injector);
    }

    private static void graph(String filename, Injector demoInjector) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos);

        Injector injector = Guice.createInjector(new GraphvizModule());
        GraphvizGrapher renderer = injector.getInstance(GraphvizGrapher.class);
        renderer.setOut(out);
        renderer.setRankdir("TB");
        renderer.graph(demoInjector);

        out = new PrintWriter(new File(filename), "UTF-8");
        String s = baos.toString("UTF-8");
        s = hideClassPaths(s);
        out.write(s);
        out.close();
    }

    private static String hideClassPaths(String s) {
        s = s.replaceAll("\\w[a-z\\d_\\.]+\\.([A-Z][A-Za-z\\d_$]*)", "$1");
        s = s.replaceAll("value=[\\w-]+", "random");
        return s;
    }
}
