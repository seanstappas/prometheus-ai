package graphing;

import tags.Tag;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Set;

class KnnForwardThinkVisualizer extends KnnGraphVisualizer {
    @Override
    String getScreenshotSavePath(String suffix) {
        return MessageFormat.format("graphs/knn/knn_forward_think_{0}.png", suffix);
    }

    @Override
    Set<Tag> search() {
        return Collections.emptySet();
    }

    public static void main(String[] args) throws InterruptedException {
        new KnnForwardThinkVisualizer().visualize(false);
    }
}
