package graphing;

import tags.Fact;
import tags.Tag;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class KnnSimpleForwardThinkVisualizer extends KnnSimpleGraphVisualizer {
    @Override
    String getScreenshotSavePath(String suffix) {
        return MessageFormat.format("graphs/knn/forward/knn_simple_forward_think_{0}", suffix);
    }

    @Override
    Set<Tag> getInitialActiveTags() {
        return new HashSet<>(Arrays.asList(
                new Fact("P1(A)", 100)
        ));
    }
    @Override
    Set<Tag> search() {
        return knn.forwardThink(1);
    }

    public static void main(String[] args) throws InterruptedException {
        new KnnSimpleForwardThinkVisualizer().visualize(true);
    }
}
