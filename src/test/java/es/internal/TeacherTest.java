package es.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tags.Fact;
import tags.Rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

public class TeacherTest {
    private Teacher teacher;
    private Set<Rule> readyRules;


    @BeforeMethod
    public void setUp() throws Exception {
        readyRules = new HashSet<>();
        teacher = new Teacher(readyRules);
    }

    @Test
    public void mustTeach() throws Exception {
        // given
        String sentence = "if P(A) then P(B)";
        Rule expectedRule = new Rule(
                new HashSet<>(Collections.singletonList(new Fact("P(A)"))),
                new HashSet<>(Collections.singletonList(new Fact("P(B)")))
        );
        Set<Rule> expectedReadyRules = new HashSet<>(
                Collections.singletonList(expectedRule));

        // when
        teacher.teach(sentence);

        // then
        assertEquals(expectedReadyRules, readyRules);
    }

}