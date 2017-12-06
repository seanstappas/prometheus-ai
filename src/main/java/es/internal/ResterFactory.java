package es.internal;

import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

interface ResterFactory {
  @Inject
  Rester create(
      @Assisted("readyRules") Set<Rule> readyRules);
}
