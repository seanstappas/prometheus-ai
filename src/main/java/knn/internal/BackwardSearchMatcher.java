package knn.internal;

import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.HashSet;
import java.util.Set;

class BackwardSearchMatcher {
    public Set<Tag> match(Set<Tag> inputTags, KnowledgeNode kn, int numRequiredMatches) {
        Set<Tag> activatedTags = new HashSet<>();
        int matchCount = 0;
        for (Tag t : inputTags) {
            if (kn.getOutputTags().contains(t)) {
                matchCount++;
            }
        }
        if (matchCount >= numRequiredMatches) {
            boolean fired = kn.excite();
            if (fired) {
                activatedTags.add(kn.getInputTag());
                activatedTags.addAll(kn.getOutputTags());
            }
        }
        return activatedTags;
    }
}
