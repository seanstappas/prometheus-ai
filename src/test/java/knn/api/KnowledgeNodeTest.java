package knn.api;

import static java.lang.System.*;
import org.testng.annotations.Test;


public class KnowledgeNodeTest {



    @Test(expectedExceptions =  KnowledgeNodeParseException.class)
    public void mustFailParsingKn() throws Exception{
        System.out.println("**Test KnowledgeNode API-- create new node**");
        new KnowledgeNode("x");
        //System.out.println("**No Exceptions**");
    }


}
