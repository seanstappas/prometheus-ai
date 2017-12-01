package tags;

import java.util.Comparator;

/**
 * Tag to be used throughout the Prometheus system. Possesses a confidence value.
 */
public abstract class Tag implements Comparable<Tag> {
    private double confidence;
    private long age = 0;

    double getConfidence() {
        return confidence;
    }

    void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    abstract String simpleToString();

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    @Override
    public int compareTo(Tag o) {
        int val = Comparator.comparing(Tag::getAge).compare(this, o);
        if (val != 0) {
            return val;
        } else {
            // TODO: handle this comparison properly
            return this.toString().compareTo(o.toString());
        }
    }
}
