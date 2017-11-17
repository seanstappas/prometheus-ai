package knn.internal;

import com.google.inject.Guice;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Tag;

import static org.mockito.Mockito.mock;

/**
 * Created by seanstappas1 on 2017-10-13.
 */
public class KnowledgeNodeNetworkImplTest {
    private KnowledgeNodeNetwork knn;

    @BeforeMethod
    public void setUp() throws Exception {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        knn = prometheus.getKnowledgeNodeNetwork();
    }

    @Test
    public void mustDirectSearch() throws Exception {
        Tag tagToActivate = mock(Tag.class);
        KnowledgeNode knToFire = mock(KnowledgeNode.class);

        // given

    }

    @Test
    public void testReset() throws Exception {
    }

    @Test
    public void testResetEmpty() throws Exception {
    }

    @Test
    public void testSaveKNN() throws Exception {
    }

    @Test
    public void testClearKN() throws Exception {
    }

    @Test
    public void testAddKN() throws Exception {
    }

    @Test
    public void testDelKN() throws Exception {
    }

    @Test
    public void testAddFiredTag() throws Exception {
    }

    @Test
    public void testGetInputTags() throws Exception {
    }

    @Test
    public void testGetActiveTags() throws Exception {
    }

    @Test
    public void testLambdaSearch() throws Exception {
    }

    @Test
    public void testBackwardSearch() throws Exception {
    }

    @Test
    public void testGetInputForBackwardSearch() throws Exception {
    }

    @Test
    public void testBackwardSearch1() throws Exception {
    }

    @Test
    public void testForwardSearch() throws Exception {
    }

    @Test
    public void testForwardSearch1() throws Exception {
    }

    @Test
    public void testGetInputForForwardSearch() throws Exception {
    }

    @Test
    public void testCreateKNfromTuple() throws Exception {
    }

    @Test
    public void testExcite() throws Exception {
    }

    @Test
    public void testFire() throws Exception {
    }

    @Test
    public void testUpdateConfidence() throws Exception {
    }

}