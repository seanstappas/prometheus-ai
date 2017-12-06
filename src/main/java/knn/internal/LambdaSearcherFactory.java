package knn.internal;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

interface LambdaSearcherFactory {
  @Inject
  LambdaSearcher create(
      @Assisted ForwardSearcher forwardSearcher,
      @Assisted BackwardSearcher backwardSearcher);
}
