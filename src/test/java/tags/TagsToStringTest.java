package tags;

import org.testng.annotations.Test;

public class TagsToStringTest {

    @Test
    public void mustRestWithNewRules() throws Exception {
        Tag t1 = new Fact("P(A)");
        System.out.println(t1.simpleToString());

        Tag t2 = new Recommendation("P(B)");
        System.out.println(t2.simpleToString());

        Rule t3 = new Rule("P -> @P");
        System.out.println(t3.getInputFacts());
        for (Fact f : t3.getInputFacts()) {
            System.out.println(f.getPredicateName());
            System.out.println(f.getArguments());
        }
        System.out.println(t3.getOutputPredicates());
        for (Predicate p : t3.getOutputPredicates()) {
            System.out.println(p.getPredicateName());
            System.out.println(p.getArguments());
        }
        System.out.println(t3.simpleToString());
    }
}
