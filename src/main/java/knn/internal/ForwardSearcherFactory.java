package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

interface ForwardSearcherFactory {
    @Inject
    ForwardSearcher create(
            @Assisted DirectSearcher directSearcher);
}
