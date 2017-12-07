package graphing;

import org.testng.annotations.Test;

public class KnnSimpleForwardThinkVisualizerTest {
    @Test
    public void visualizeWithoutDisplay() throws Exception {
        final KnnSimpleForwardThinkVisualizer visualizer =
                new KnnSimpleForwardThinkVisualizer();
        visualizer.setSleepDelay(0);
        visualizer.setUpdated(true);
        visualizer.visualize(false, false);
    }
}
