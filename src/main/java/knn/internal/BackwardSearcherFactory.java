package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

import java.util.Set;
import java.util.TreeSet;

interface BackwardSearcherFactory {
    @Inject
    BackwardSearcher create(
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs,
            @Assisted("partialMatchRatio") double partialMatchRatio,
            @Assisted("ageLimit") long ageLimit);
}
