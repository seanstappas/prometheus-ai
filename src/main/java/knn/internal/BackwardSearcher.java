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
    BackwardSearcher(
            @Assisted("activeTags") final Set<Tag> activeTags,
            @Assisted("ageSortedKNs") final TreeSet<KnowledgeNode> ageSortedKNs,
            @Assisted("partialMatchRatio") final double partialMatchRatio,
            @Assisted("ageLimit") final long ageLimit,
            final BackwardSearchMatcher backwardSearchMatcher) {
        this.activeTags = activeTags;
        this.partialMatchRatio = partialMatchRatio;
        this.ageLimit = ageLimit;
        this.backwardSearchMatcher = backwardSearchMatcher;
        this.ageSortedKNs = ageSortedKNs;
    }

    /**
     * Sets the partial match ratio for backward search.
     *
     * @param partialMatchRatio the partial match ratio
     */
    void setPartialMatchRatio(final double partialMatchRatio) {
        this.partialMatchRatio = partialMatchRatio;
    }

    /**
     * Sets the KN age limit for backward search.
     *
     * @param ageLimit the age limit
     */
    void setAgeLimit(final long ageLimit) {
        this.ageLimit = ageLimit;
    }

    @Override
    public Set<Tag> searchInternal(final Set<Tag> inputTags, final double ply) {
        final Set<Tag> allActivatedTags = new HashSet<>();
        Set<Tag> currentPlyInputTags = new HashSet<>(inputTags);
        for (int i = 0; i < ply && !currentPlyInputTags.isEmpty(); i++) {
            final int numRequiredMatches =
                    (int) (partialMatchRatio * currentPlyInputTags.size());
            final Set<Tag> activatedTags = new HashSet<>();
            // Iterate over the KNs in order of increasing age
            for (final KnowledgeNode kn : ageSortedKNs) {
                if (kn.getCurrentAge() > ageLimit) { // Age limit reached.
                    break;
                }
                backwardSearchMatcher
                        .match(currentPlyInputTags, kn, numRequiredMatches)
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
