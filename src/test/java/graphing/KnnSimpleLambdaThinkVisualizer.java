package graphing;

import tags.Fact;
import tags.Tag;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class KnnSimpleLambdaThinkVisualizer extends KnnSimpleGraphVisualizer {
  private boolean shouldBackwardSearch = true;

  public static void main(String[] args) throws InterruptedException {
    new KnnSimpleLambdaThinkVisualizer().visualize(false);
  }

  @Override
  String getScreenshotSavePath(String suffix) {
    return MessageFormat
        .format("graphs/knn/lambda/knn_simple_lambda_think_{0}", suffix);
  }

  @Override
  Set<Tag> getInitialActiveTags() {
    return new HashSet<>(Collections.singletonList(
        new Fact("P7(A)", 100)
    ));
  }

  @Override
  Set<Tag> search() {
    if (shouldBackwardSearch) {
      knn.setBackwardSearchMatchRatio(1d / knn.getActiveTags().size());
      Set<Tag> backwardTags = knn.backwardThink(1);
      shouldBackwardSearch = !backwardTags.isEmpty();
      forceContinue = true;
      return backwardTags;
    } else {
      forceContinue = false;
      return knn.forwardThink(1);
    }
  }
}
