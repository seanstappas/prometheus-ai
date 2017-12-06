package graphing;

import java.util.HashSet;
import java.util.Set;
import tags.Tag;

public class GenericGraphCreator extends KnnGraphVisualizer {
  public static void main(String[] args) throws InterruptedException {
    new GenericGraphCreator().visualize(false);
  }

  @Override
  String getScreenshotSavePath(String suffix) {
    return "graphs/knn/knn_graph.png";
  }

  @Override
  Set<Tag> search() {
    return new HashSet<>();
  }
}
