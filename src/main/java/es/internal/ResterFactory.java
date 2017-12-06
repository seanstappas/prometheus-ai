package es.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

import java.util.Set;

interface ResterFactory {
  @Inject
  Rester create(
      @Assisted("readyRules") Set<Rule> readyRules);
}
