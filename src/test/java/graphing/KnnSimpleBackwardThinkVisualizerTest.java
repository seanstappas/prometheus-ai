package graphing;

import org.testng.annotations.Test;

public class KnnSimpleBackwardThinkVisualizerTest {
    @Test
    public void visualizeWithoutDisplay() throws Exception {
        final KnnSimpleBackwardThinkVisualizer visualizer =
                new KnnSimpleBackwardThinkVisualizer();
        visualizer.setSleepDelay(0);
        visualizer.setUpdated(true);
        visualizer.visualize(false, false);
    }
}
