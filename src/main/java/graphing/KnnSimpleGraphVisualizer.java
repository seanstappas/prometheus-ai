package graphing;

abstract class KnnSimpleGraphVisualizer extends KnnGraphVisualizer {
    private static final String SIMPLE_DATA_PATH = "data/simpleData.txt";


    @Override
    String getKnnDataPath() {
        return SIMPLE_DATA_PATH;
    }
}
