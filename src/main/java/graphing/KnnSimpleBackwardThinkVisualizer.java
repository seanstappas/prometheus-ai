package graphing;

import tags.Fact;
import tags.Tag;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class KnnSimpleBackwardThinkVisualizer extends KnnSimpleGraphVisualizer {
    @Override
    String getScreenshotSavePath(String suffix) {
        return MessageFormat.format("graphs/knn/backward/knn_simple_backward_think_{0}", suffix);
    }

    @Override
    Set<Tag> getInitialActiveTags() {
        return new HashSet<>(Arrays.asList(
                new Fact("P7(A)", 100)
        ));
    }

    @Override
    Set<Tag> search() {
        knn.setBackwardSearchMatchRatio(1d / knn.getActiveTags().size());
        return knn.backwardThink(1);
    }

    public static void main(String[] args) throws InterruptedException {
        new KnnSimpleBackwardThinkVisualizer().visualize(true);
    }
}
