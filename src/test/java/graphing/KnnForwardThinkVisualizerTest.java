package graphing;

import org.testng.annotations.Test;

public class KnnForwardThinkVisualizerTest {
    @Test
    public void visualizeWithoutDisplay() throws Exception {
        final KnnForwardThinkVisualizer visualizer =
                new KnnForwardThinkVisualizer();
        visualizer.setSleepDelay(0);
        visualizer.setUpdated(true);
        visualizer.visualize(false, false);
    }
}
