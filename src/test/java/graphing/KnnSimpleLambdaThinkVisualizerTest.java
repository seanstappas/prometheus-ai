package graphing;

import org.testng.annotations.Test;

public class KnnSimpleLambdaThinkVisualizerTest {
    @Test
    public void visualizeWithoutDisplay() throws Exception {
        final KnnSimpleLambdaThinkVisualizer visualizer =
                new KnnSimpleLambdaThinkVisualizer();
        visualizer.setSleepDelay(0);
        visualizer.setUpdated(true);
        visualizer.visualize(false, false);
    }
}
