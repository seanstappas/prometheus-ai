package es.internal;

import es.api.ExpertSystem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Predicate;
import tags.Rule;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * Created by seanstappas1 on 2017-10-13.
 */
public class ExpertSystemImplTest {
    private ExpertSystem es;

    @BeforeMethod
    public void setUp() throws Exception {
        es = new ExpertSystemImpl();
    }

    @Test
    public void testReset() throws Exception {
        es.reset();

        assertTrue(es.getActiveRules().isEmpty());
        assertTrue(es.getReadyRules().isEmpty());
        assertTrue(es.getFacts().isEmpty());
        assertTrue(es.getRecommendations().isEmpty());
    }

    @Test
    public void testDeactivateRules() throws Exception {
        Rule rule = mock(Rule.class);

        es.addRule(rule);

        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        System.out.println(set);
        set.removeAll(set);
        System.out.println(set);

        Set<Rule> expectedRestRules = new HashSet<>();
        expectedRestRules.add(new Rule(
                new Fact[]{
                        new Fact("Goose(loud,nationality=canadian,wingspan=4)"),
                        new Fact("Aardvark(brown,?,speed=slow)")},
                new Predicate[]{
                        new Fact("Hog(colour=green,size=huge,sound=ribbit,big)")})
        );
        System.out.println(expectedRestRules);
        expectedRestRules.removeAll(expectedRestRules);
        System.out.println(expectedRestRules);


    }

    @Test
    public void testAddTags() throws Exception {
    }

    @Test
    public void testAddFact() throws Exception {
    }

    @Test
    public void testRemoveFact() throws Exception {
    }

    @Test
    public void testAddRule() throws Exception {
    }

    @Test
    public void testAddRecommendation() throws Exception {
    }

    @Test
    public void testGetRecommendations() throws Exception {
    }

    @Test
    public void testGetReadyRules() throws Exception {
    }

    @Test
    public void testGetActiveRules() throws Exception {
    }

    @Test
    public void testGetFacts() throws Exception {
    }

    @Test
    public void testThink() throws Exception {
    }

    @Test
    public void testThink1() throws Exception {
    }

    @Test
    public void testThink2() throws Exception {
    }

    @Test
    public void testRest() throws Exception {
    }

    @Test
    public void testTeach() throws Exception {
    }

}