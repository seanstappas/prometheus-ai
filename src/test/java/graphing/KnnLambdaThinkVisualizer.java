package graphing;

import tags.Tag;

import java.text.MessageFormat;
import java.util.Set;

class KnnLambdaThinkVisualizer extends KnnGraphVisualizer {
    boolean swap = true;
    Set<Tag> backwardTags;

    @Override
    String getScreenshotSavePath(String suffix) {
        return MessageFormat.format("graphs/knn_lambda_think_{0}.png", suffix);
    }

    @Override
    Set<Tag> search() {
        knn.setBackwardSearchMatchRatio(1d / knn.getActiveTags().size());

        if (swap) {
            backwardTags = knn.backwardThink(1);
            swap = !swap;
            return backwardTags;
        }
        else {
            swap = !swap;
            return knn.forwardSearch(backwardTags, 1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new KnnLambdaThinkVisualizer().visualize();
    }
}
