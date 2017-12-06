package graphing;

import java.text.MessageFormat;
import java.util.Set;
import tags.Tag;

class KnnBackwardThinkVisualizer extends KnnGraphVisualizer {
  public static void main(String[] args) throws InterruptedException {
    new KnnBackwardThinkVisualizer().visualize(false);
  }

  @Override
  String getScreenshotSavePath(String suffix) {
    return MessageFormat
        .format("graphs/knn/backward/knn_backward_think_{0}", suffix);
  }

  @Override
  Set<Tag> search() {
    knn.setBackwardSearchMatchRatio(1d / knn.getActiveTags().size());
    return knn.backwardThink(1);
  }
}
