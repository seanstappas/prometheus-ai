package knn.api;

/**
 * Exception thrown when parsing a Knowledge Node from String data fails.
 */
public class KnowledgeNodeParseException extends Exception {
    public KnowledgeNodeParseException(String msg) {
        super(msg);
    }
}
