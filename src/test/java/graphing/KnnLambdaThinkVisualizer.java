package graphing;

import java.text.MessageFormat;
import java.util.Set;
import tags.Tag;

/**
 * KNN lambda think visualizer.
 */
class KnnLambdaThinkVisualizer extends KnnGraphVisualizer {
    private boolean swap = true;
    private Set<Tag> backwardTags;

    @Override
    String getScreenshotSavePath(final String suffix) {
        return MessageFormat
                .format("graphs/knn/lambda/knn_lambda_think_{0}", suffix);
    }

    @Override
    Set<Tag> search() {
        getKnn().setBackwardSearchMatchRatio(
                1d / getKnn().getActiveTags().size());

        if (swap) {
            backwardTags = getKnn().backwardThink(1);
            swap = !swap;
            return backwardTags;
        } else {
            swap = true;
            return getKnn().forwardSearch(backwardTags, 1);
        }
    }

    public static void main(final String[] args) throws InterruptedException {
        new KnnLambdaThinkVisualizer().visualize(false, true);
    }
}
