package graphing;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import tags.Fact;
import tags.Tag;

/**
 * KNN simple forward think visualizer.
 */
class KnnSimpleForwardThinkVisualizer extends KnnSimpleGraphVisualizer {
    @Override
    String getScreenshotSavePath(final String suffix) {
        return MessageFormat
                .format("graphs/knn/forward/knn_simple_forward_think_{0}",
                        suffix);
    }

    @Override
    Set<Tag> getInitialActiveTags() {
        return new HashSet<>(Collections.singletonList(
                new Fact("P1(A)")
        ));
    }

    @Override
    Set<Tag> search() {
        return getKnn().forwardThink(1);
    }

    public static void main(final String[] args) throws InterruptedException {
        new KnnSimpleForwardThinkVisualizer().visualize(false, true);
    }
}
