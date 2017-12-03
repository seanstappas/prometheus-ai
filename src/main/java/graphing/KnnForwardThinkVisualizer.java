package graphing;

import tags.Tag;

import java.text.MessageFormat;
import java.util.Set;

class KnnForwardThinkVisualizer extends KnnGraphVisualizer {
    @Override
    String getScreenshotSavePath(String suffix) {
        return MessageFormat.format("graphs/knn/forward/knn_forward_think_{0}", suffix);
    }

    @Override
    Set<Tag> search() {
        return knn.forwardThink(1);
    }

    public static void main(String[] args) throws InterruptedException {
        new KnnForwardThinkVisualizer().visualize(false);
    }
}
