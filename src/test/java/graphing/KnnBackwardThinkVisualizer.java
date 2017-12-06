package graphing;

import tags.Tag;

import java.text.MessageFormat;
import java.util.Set;

class KnnBackwardThinkVisualizer extends KnnGraphVisualizer {
    @Override
    String getScreenshotSavePath(String suffix) {
        return MessageFormat.format("graphs/knn/backward/knn_backward_think_{0}", suffix);
    }

    @Override
    Set<Tag> search() {
        knn.setBackwardSearchMatchRatio(1d / knn.getActiveTags().size());
        return knn.backwardThink(1);
    }

    public static void main(String[] args) throws InterruptedException {
        new KnnBackwardThinkVisualizer().visualize(false);
    }
}
