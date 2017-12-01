package tags;

import org.testng.annotations.Test;

public class TagsToStringTest {

    @Test
    public void mustRestWithNewRules() throws Exception {
        Tag t1 = new Fact("P(A)");
        System.out.println(t1.simpleToString());

        Tag t2 = new Recommendation("P(B)");
        System.out.println(t2.simpleToString());

        Tag t3 = new Rule("P(A) -> @P(B)");
        System.out.println(t3.simpleToString());
    }
}
