package graphing;

import java.util.HashSet;
import java.util.Set;
import tags.Tag;

/**
 * Creates a generic KNN graph without searching through it.
 */
public final class GenericGraphCreator extends KnnGraphVisualizer {
    public static void main(final String[] args) throws InterruptedException {
        new GenericGraphCreator().visualize(false, true);
    }

    @Override
    String getScreenshotSavePath(final String suffix) {
        return "graphs/knn/knn_graph.png";
    }

    @Override
    Set<Tag> search() {
        return new HashSet<>();
    }
}
