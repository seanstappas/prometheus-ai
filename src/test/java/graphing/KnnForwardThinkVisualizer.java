package graphing;

import java.text.MessageFormat;
import java.util.Set;
import tags.Tag;

class KnnForwardThinkVisualizer extends KnnGraphVisualizer {
  public static void main(String[] args) throws InterruptedException {
    new KnnForwardThinkVisualizer().visualize(false);
  }

  @Override
  String getScreenshotSavePath(String suffix) {
    return MessageFormat
        .format("graphs/knn/forward/knn_forward_think_{0}", suffix);
  }

  @Override
  Set<Tag> search() {
    return knn.forwardThink(1);
  }
}
