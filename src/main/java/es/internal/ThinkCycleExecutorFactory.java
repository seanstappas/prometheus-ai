package es.internal;

import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;

interface ThinkCycleExecutorFactory {
  @Inject
  ThinkCycleExecutor create(
      @Assisted("readyRules") Set<Rule> readyRules,
      @Assisted("activeRules") Set<Rule> activeRules,
      @Assisted("facts") Set<Fact> facts,
      @Assisted("recommendations") Set<Recommendation> recommendations);
}
