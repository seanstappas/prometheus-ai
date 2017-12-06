package es.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

import java.util.Set;

interface TeacherFactory {
  @Inject
  Teacher create(
      @Assisted("readyRules") Set<Rule> readyRules);
}
