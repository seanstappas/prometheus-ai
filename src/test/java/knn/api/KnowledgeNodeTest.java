package knn.api;

import org.testng.annotations.Test;

public class KnowledgeNodeTest {
    @Test(expectedExceptions =  KnowledgeNodeParseException.class)
    public void mustFailParsingKn() throws Exception{
        new KnowledgeNode("x");
    }
}
