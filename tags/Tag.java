package tags;

/**
 * Created by Sean on 3/18/2017.
 */
public class Tag {
    public String value;
    public TagType type;

    Tag() {
    }

    public Tag(String value, TagType type) {
        this.value = value;
        this.type = type;
    }

    public boolean isRecommendation() {
        return type == TagType.RECOMMENDATION;
    }

    public boolean isFact() {
        return type == TagType.FACT;
    }

    public boolean isRule() {
        return type == TagType.RULE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
