package knn.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import tags.Tag;

/**
 * Searcher which performs direct search in the KNN.
 */
class DirectSearcher {
  private final Map<Tag, KnowledgeNode> mapKN;
  private final Set<Tag> activeTags;
  private final TreeSet<KnowledgeNode> ageSortedKNs;

  @Inject
  public DirectSearcher(
      @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
      @Assisted("activeTags") Set<Tag> activeTags,
      @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs) {
    this.mapKN = mapKN;
    this.activeTags = activeTags;
    this.ageSortedKNs = ageSortedKNs;
  }

  /**
   * Search for the given input Tag in the KN map.
   *
   * @param inputTag the input Tag to search for
   * @return the activated output tags (excluding the given input Tag)
   */
  Set<Tag> search(Tag inputTag) {
    Set<Tag> activatedTags = new HashSet<>();
    if (mapKN.containsKey(inputTag)) {
      KnowledgeNode kn = mapKN.get(inputTag);
      ageSortedKNs.remove(kn);
      boolean fired = kn.excite();
      ageSortedKNs.add(kn);
      if (fired) {
        activatedTags.addAll(kn.getOutputTags());
      } else if (kn.isExpired()) {
        mapKN.remove(kn.getInputTag());
        activeTags.remove(kn.getInputTag());
        ageSortedKNs.remove(kn);
      }
    }
    this.activeTags.add(inputTag);
    this.activeTags.addAll(activatedTags);
    return Collections.unmodifiableSet(activatedTags);
  }
}
