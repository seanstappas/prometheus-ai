package graphing;

import java.text.MessageFormat;
import java.util.Set;
import tags.Tag;

/**
 * KNN backward think visualizer.
 */
class KnnBackwardThinkVisualizer extends KnnGraphVisualizer {
    @Override
    String getScreenshotSavePath(final String suffix) {
        return MessageFormat
                .format("graphs/knn/backward/knn_backward_think_{0}", suffix);
    }

    @Override
    Set<Tag> search() {
        getKnn().setBackwardSearchMatchRatio(
                1d / getKnn().getActiveTags().size());
        return getKnn().backwardThink(1);
    }

    public static void main(final String[] args) throws InterruptedException {
        new KnnBackwardThinkVisualizer().visualize(false, true);
    }
}
