package es.internal;

import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

/**
 * Factory to create a Teacher.
 */
interface TeacherFactory {
    /**
     * Creates a Teacher.
     *
     * @param readyRules the ready rules of the ES
     * @return the created Teacher
     */
    @Inject
    Teacher create(
            @Assisted("readyRules") Set<Rule> readyRules);
}
