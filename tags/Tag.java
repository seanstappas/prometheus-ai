package tags;

/**
 * Created by Sean on 3/18/2017.
 */
public class Tag {
    public String value;
    public Type type;
    public enum Type {
        FACT,
        RULE,
        RECOMMENDATION
    }

    Tag() {
    }

    public Tag(String value, Type type) {
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
        if (o == null || !(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return (value != null ? value.equals(tag.value) : tag.value == null) && type == tag.type;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value;
    }
}
