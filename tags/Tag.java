package tags;

/**
 * Created by Sean on 3/18/2017.
 */
public class Tag { // TODO: Add Type enumeration to differentiate Tags
    public String value;
    public boolean isReccomendation;

    public Tag(String value, boolean isReccomendation) {
        this.value = value;
        this.isReccomendation = isReccomendation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return isReccomendation == tag.isReccomendation && (value != null ? value.equals(tag.value) : tag.value == null);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (isReccomendation ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return value + (isReccomendation ? "(recommendation)" : "");
    }
}
