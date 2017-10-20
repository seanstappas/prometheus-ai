package es.api;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;

import java.util.Set;

public interface ExpertSystemFactory {
    @Inject
    ExpertSystem create(
            @Assisted("readyRules") Set<Rule> readyRules,
            @Assisted("activeRules") Set<Rule> activeRules,
            @Assisted("facts") Set<Fact> facts,
            @Assisted("recommendations") Set<Recommendation> recommendations);
}
