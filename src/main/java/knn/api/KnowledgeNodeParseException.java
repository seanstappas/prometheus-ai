package knn.api;

/**
 * Exception thrown when parsing a Knowledge Node from String data fails.
 */
public class KnowledgeNodeParseException extends Exception {
    public KnowledgeNodeParseException(final String msg) {
        super(msg);
    }
}
