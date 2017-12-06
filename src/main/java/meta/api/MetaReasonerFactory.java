package meta.api;

import com.google.inject.Inject;

public interface MetaReasonerFactory {
    @Inject
    MetaReasoner create();
}
