package graphing;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import tags.Fact;
import tags.Tag;

/**
 * KNN simple lambda think visualizer.
 */
class KnnSimpleLambdaThinkVisualizer extends KnnSimpleGraphVisualizer {
    private boolean shouldBackwardSearch = true;

    @Override
    String getScreenshotSavePath(final String suffix) {
        return MessageFormat
                .format("graphs/knn/lambda/knn_simple_lambda_think_{0}",
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
        if (shouldBackwardSearch) {
            getKnn().setBackwardSearchMatchRatio(
                    1d / getKnn().getActiveTags().size());
            final Set<Tag> backwardTags = getKnn().backwardThink(1);
            shouldBackwardSearch = !backwardTags.isEmpty();
            setForceContinue(true);
            return backwardTags;
        } else {
            setForceContinue(false);
            return getKnn().forwardThink(1);
        }
    }

    public static void main(final String[] args) throws InterruptedException {
        new KnnSimpleLambdaThinkVisualizer().visualize(false, true);
    }
}
