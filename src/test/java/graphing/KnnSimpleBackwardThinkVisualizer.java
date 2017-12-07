package graphing;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import tags.Fact;
import tags.Tag;

/**
 * KNN simple backward think visualizer.
 */
class KnnSimpleBackwardThinkVisualizer extends KnnSimpleGraphVisualizer {
    @Override
    String getScreenshotSavePath(final String suffix) {
        return MessageFormat
                .format("graphs/knn/backward/knn_simple_backward_think_{0}",
                        suffix);
    }

    @Override
    Set<Tag> getInitialActiveTags() {
        return new HashSet<>(Collections.singletonList(
                new Fact("P7(A)")
        ));
    }

    @Override
    Set<Tag> search() {
        getKnn().setBackwardSearchMatchRatio(
                1d / getKnn().getActiveTags().size());
        return getKnn().backwardThink(1);
    }

    public static void main(final String[] args) throws InterruptedException {
        new KnnSimpleBackwardThinkVisualizer().visualize(false, true);
    }
}
