package graphing;

import java.text.MessageFormat;
import java.util.Set;
import tags.Tag;

/**
 * KNN forward think visualizer.
 */
class KnnForwardThinkVisualizer extends KnnGraphVisualizer {
    @Override
    String getScreenshotSavePath(final String suffix) {
        return MessageFormat
                .format("graphs/knn/forward/knn_forward_think_{0}", suffix);
    }

    @Override
    Set<Tag> search() {
        return getKnn().forwardThink(1);
    }

    public static void main(final String[] args) throws InterruptedException {
        new KnnForwardThinkVisualizer().visualize(false, true);
    }
}
