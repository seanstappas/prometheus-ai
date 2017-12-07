package graphing;

import org.testng.annotations.Test;

public class KnnLambdaThinkVisualizerTest {
    @Test
    public void visualizeWithoutDisplay() throws Exception {
        final KnnLambdaThinkVisualizer visualizer =
                new KnnLambdaThinkVisualizer();
        visualizer.setSleepDelay(0);
        visualizer.setUpdated(true);
        visualizer.visualize(false, false);
    }
}
