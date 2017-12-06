package knn.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

/**
 * Searcher which performs backward search in the KNN.
 */
class BackwardSearcher extends Searcher<Set<Tag>> {
  private final Set<Tag> activeTags;
  private final BackwardSearchMatcher backwardSearchMatcher;
  private final TreeSet<KnowledgeNode> ageSortedKNs;
  private double partialMatchRatio;
  private long ageLimit;

  @Inject
  public BackwardSearcher(
      @Assisted("activeTags") Set<Tag> activeTags,
      @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs,
      @Assisted("partialMatchRatio") double partialMatchRatio,
      @Assisted("ageLimit") long ageLimit,
      BackwardSearchMatcher backwardSearchMatcher) {
    this.activeTags = activeTags;
    this.partialMatchRatio = partialMatchRatio;
    this.ageLimit = ageLimit;
    this.backwardSearchMatcher = backwardSearchMatcher;
    this.ageSortedKNs = ageSortedKNs;
  }

  void setMatchRatio(double partialMatchRatio) {
    this.partialMatchRatio = partialMatchRatio;
  }

  void setAgeLimit(long ageLimit) {
    this.ageLimit = ageLimit;
  }

  @Override
  public Set<Tag> searchInternal(Set<Tag> inputTags, double ply) {
    Set<Tag> allActivatedTags = new HashSet<>();
    Set<Tag> currentPlyInputTags = new HashSet<>(inputTags);
    for (int i = 0; i < ply && !currentPlyInputTags.isEmpty(); i++) {
      int numRequiredMatches =
          (int) (partialMatchRatio * currentPlyInputTags.size());
      Set<Tag> activatedTags = new HashSet<>();
      // Iterate over the KNs in order of increasing age
      for (KnowledgeNode kn : ageSortedKNs) {
        if (kn.getCurrentAge() > ageLimit) { // Age limit reached.
          break;
        }
        backwardSearchMatcher.match(currentPlyInputTags, kn, numRequiredMatches)
            .ifPresent(activatedTags::add);
      }
      allActivatedTags.addAll(activatedTags);
      currentPlyInputTags = activatedTags;
    }
    allActivatedTags.removeIf(this.activeTags::contains);
    this.activeTags.addAll(allActivatedTags);
    return Collections.unmodifiableSet(allActivatedTags);
  }
}
