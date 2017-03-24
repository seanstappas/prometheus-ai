package interfaces;

import tags.Tag;

import java.util.Set;

/**
 * Interface for the common functionality between the Expert System and Knowledge Node Network.
 */
public interface PrometheusLayer {
    Set<Tag> think();
}
