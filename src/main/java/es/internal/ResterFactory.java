package es.internal;

import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import tags.Rule;

/**
 * Guice factory for the Rester.
 */
interface ResterFactory {
    /**
     * Creates a Rester.
     *
     * @param readyRules the ready Rules
     * @return the created Rester
     */
    @Inject
    Rester create(
            @Assisted("readyRules") Set<Rule> readyRules);
}
