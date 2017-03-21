package tags;

/**
 * Created by Sean on 3/18/2017.
 */
public class Tag { // TODO: Add Type enumeration to differentiate Tags
    public String value;
    private Type type;
    public enum Type {
        FACT,
        RULE,
        RECOMMENDATION
    }

    Tag(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public boolean isRecommendation() {
        return type == Type.RECOMMENDATION;
    }

    public boolean isFact() {
        return type == Type.FACT;
    }

    public boolean isRule() {
        return type == Type.RULE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return value != null ? value.equals(tag.value) : tag.value == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result;
        return result;
    }

    @Override
    public String toString() {
        return value;
    }
}
