package graphing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import prometheus.guice.PrometheusModule;

/**
 * Visualize the Guice dependencies.
 */
public final class GuiceDependencyVisualizer {
    private static final String GRAPH_SAVE_PATH =
            "graphs/guice/guice_graph.dot";

    private GuiceDependencyVisualizer() {
    }

    public static void main(final String[] args) throws IOException {
        final Injector injector = Guice.createInjector(new PrometheusModule());
        graph(GRAPH_SAVE_PATH, injector);
    }

    /**
     * Creates the Guice dependency graph.
     *
     * @param filename     the name of the file to save the graph to
     * @param demoInjector the Guice injector to visualize
     * @throws IOException if saving to file fails
     */
    private static void graph(final String filename,
                              final Injector demoInjector) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(baos, StandardCharsets.UTF_8), true);

        final Injector injector = Guice.createInjector(new GraphvizModule());
        final GraphvizGrapher renderer =
                injector.getInstance(GraphvizGrapher.class);
        renderer.setOut(out);
        renderer.setRankdir("TB");
        renderer.graph(demoInjector);

        out = new PrintWriter(new File(filename), "UTF-8");
        String s = baos.toString("UTF-8");
        s = hideClassPaths(s);
        out.write(s);
        out.close();
    }

    /**
     * Hides the class paths in the generated graph string.
     *
     * @param s the graph string
     * @return the modified graph string
     */
    private static String hideClassPaths(final String s) {
        String s2 = s.replaceAll("\\w[a-z\\d_\\.]+\\.([A-Z][A-Za-z\\d_$]*)",
                "$1");
        s2 = s2.replaceAll("value=[\\w-]+", "random");
        return s2;
    }
}
