package knn.internal;

import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.Optional;
import java.util.Set;

class BackwardSearchMatcher {
    public Optional<Tag> match(Set<Tag> inputTags, KnowledgeNode kn, int numRequiredMatches) {
        int matchCount = 0;
        for (Tag t : inputTags) {
            if (kn.getOutputTags().contains(t)) {
                matchCount++;
            }
        }
        if (matchCount >= numRequiredMatches) {
            boolean fired = kn.excite();
            if (fired) {
                return Optional.of(kn.getInputTag());
            }
        }
        return Optional.empty();
    }
}
