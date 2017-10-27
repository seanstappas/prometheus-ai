package es.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Rule;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

public class ResterTest {
    private Set<Rule> readyRules;
    private RuleMerger ruleMerger;
    private Rester rester;

    @BeforeMethod
    public void setUp() throws Exception {
        readyRules = new HashSet<>();
        ruleMerger = mock(RuleMerger.class);
        rester = new Rester(readyRules, ruleMerger);
    }

    @Test
    public void mustRestWithNewRules() throws Exception {
        Rule mergedRule = mock(Rule.class);

        // given
        when(ruleMerger.makeMergedRule(readyRules)).thenReturn(Optional.of(mergedRule));

        // when
        rester.rest(1);

        // then
        assertTrue(readyRules.contains(mergedRule));
    }

}