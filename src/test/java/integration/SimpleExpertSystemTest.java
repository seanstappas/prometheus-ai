package integration;

import com.google.inject.Guice;
import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import prometheus.api.Prometheus;
import prometheus.guice.PrometheusModule;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

public class SimpleExpertSystemTest {
    private ExpertSystem es;

    @BeforeMethod
    public void setUp() throws Exception {
        Prometheus prometheus = Guice.createInjector(new PrometheusModule()).getInstance(Prometheus.class);
        es = prometheus.getExpertSystem();
    }

    @Test
    public void simpleExpertSystemTest() {
        es.addReadyRule(new Rule("A(*), B(*) -> D(*)"));
        es.addReadyRule(new Rule("D(*), B(*) -> E(*)"));
        es.addReadyRule(new Rule("D(*), E(*) -> F(*)"));
        es.addReadyRule(new Rule("G(*), A(*) -> H(*)"));
        es.addReadyRule(new Rule("E(*), F(*) -> @Z(*)"));

        es.addFact(new Fact("A(*)"));
        es.addFact(new Fact("B(*)"));

        es.addRecommendation(new Recommendation("@X(*)"));
        es.addRecommendation(new Recommendation("@Y(*)"));

        try {
            es.think();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Set<Rule> expectedReadyRules = new HashSet<>(Collections.singletonList(new Rule("G(*), A(*) -> H(*)")));
        Set<Rule> expectedActiveRules = new HashSet<>(Arrays.asList(
                new Rule("A(*), B(*) -> D(*)"),
                new Rule("D(*), B(*) -> E(*)"),
                new Rule("D(*), E(*) -> F(*)"),
                new Rule("E(*), F(*) -> @Z(*)")));
        Set<Fact> expectedActiveFacts = new HashSet<>(Arrays.asList(
                new Fact("A(*)"),
                new Fact("B(*)"),
                new Fact("D(*)"),
                new Fact("E(*)"),
                new Fact("F(*)")));
        Set<Recommendation> expectedActiveRecommendations = new HashSet<>(Arrays.asList(
                new Recommendation("@X(*)"),
                new Recommendation("@Y(*)"),
                new Recommendation("@Z(*)")));

        assertEquals(expectedReadyRules, es.getReadyRules());
        assertEquals(expectedActiveRules, es.getActiveRules());
        assertEquals(expectedActiveFacts, es.getFacts());
        assertEquals(expectedActiveRecommendations, es.getRecommendations());
    }
}
