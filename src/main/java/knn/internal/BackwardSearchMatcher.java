package knn.internal;

import java.util.Optional;
import java.util.Set;
import knn.api.KnowledgeNode;
import tags.Tag;

/**
 * Matcher which checks if a given KN matches a set of input Tags. Used during
 * backward search in the KNN.
 */
class BackwardSearchMatcher {
    /**
     * Checks if the given KN's output Tags match the given input Tags,
     * according to the given number of required matches.
     *
     * @param inputTags          the input Tags
     * @param kn                 the Knowledge Node
     * @param numRequiredMatches the number of required matches between the
     *                           output Tags and the Set of input Tags
     * @return the activated input Tag of the KN if there is a match, otherwise
     * an empty Optional object.
     */
    public Optional<Tag> match(final Set<Tag> inputTags, final KnowledgeNode kn,
                               final int numRequiredMatches) {
        int matchCount = 0;
        for (final Tag t : inputTags) {
            if (kn.getOutputTags().contains(t)) {
                matchCount++;
            }
        }
        if (matchCount >= numRequiredMatches) {
            return Optional.of(kn.getInputTag());
        }
        return Optional.empty();
    }
}
