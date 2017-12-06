package knn.internal;

import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.Optional;
import java.util.Set;

/**
 * Matcher which checks if a given KN matches a set of input Tags. Used during
 * backward search in the KNN.
 */
class BackwardSearchMatcher {
  /**
   * Checks if the given KN's output Tags match the given input Tags, according
   * to the given number of required matches.
   *
   * @param inputTags          the input Tags
   * @param kn                 the Knowledge Node
   * @param numRequiredMatches the number of required matches between the output Tags and the Set of
   *                           input Tags
   * @return the activated input Tag of the KN if there is a match, otherwise an empty Optional
   * object.
   */
  public Optional<Tag> match(Set<Tag> inputTags, KnowledgeNode kn,
                             int numRequiredMatches) {
    int matchCount = 0;
    for (Tag t : inputTags) {
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
