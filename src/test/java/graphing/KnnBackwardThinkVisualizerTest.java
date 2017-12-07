package graphing;

import org.testng.annotations.Test;

public class KnnBackwardThinkVisualizerTest {
    @Test
    public void visualizeWithoutDisplay() throws Exception {
        final KnnBackwardThinkVisualizer visualizer =
                new KnnBackwardThinkVisualizer();
        visualizer.setSleepDelay(0);
        visualizer.setUpdated(true);
        visualizer.visualize(false, false);
    }
}
