package graphing;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import tags.Fact;
import tags.Tag;

class KnnSimpleBackwardThinkVisualizer extends KnnSimpleGraphVisualizer {
  public static void main(String[] args) throws InterruptedException {
    new KnnSimpleBackwardThinkVisualizer().visualize(false);
  }

  @Override
  String getScreenshotSavePath(String suffix) {
    return MessageFormat
        .format("graphs/knn/backward/knn_simple_backward_think_{0}", suffix);
  }

  @Override
  Set<Tag> getInitialActiveTags() {
    return new HashSet<>(Collections.singletonList(
        new Fact("P7(A)", 100)
    ));
  }

  @Override
  Set<Tag> search() {
    knn.setBackwardSearchMatchRatio(1d / knn.getActiveTags().size());
    return knn.backwardThink(1);
  }
}
