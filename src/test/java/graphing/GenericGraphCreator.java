package graphing;

import tags.Tag;

import java.util.HashSet;
import java.util.Set;

public class GenericGraphCreator extends KnnGraphVisualizer {
    @Override
    String getScreenshotSavePath(String suffix) {
        return "graphs/knn/knn_graph.png";
    }

    @Override
    Set<Tag> search() {
        return new HashSet<>();
    }

    public static void main(String[] args) throws InterruptedException {
        new GenericGraphCreator().visualize(true);
    }
}
