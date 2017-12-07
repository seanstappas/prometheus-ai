package graphing;

/**
 * Simple visualizer of the KNN with a specified style sheet and data path.
 */
abstract class KnnSimpleGraphVisualizer extends KnnGraphVisualizer {
    private static final String SIMPLE_KNN_STYLE_SHEET_PATH =
            "graphs/knn/knn_graph_style_sheet_big.css";
    private static final String SIMPLE_DATA_PATH = "data/simpleData.txt";


    @Override
    String getStyleSheetPath() {
        return SIMPLE_KNN_STYLE_SHEET_PATH;
    }

    @Override
    String getKnnDataPath() {
        return SIMPLE_DATA_PATH;
    }
}
