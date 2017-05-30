package tags;

import java.util.Set;

/**
 * Represents a fact in the Expert System. Facts are simple calculus predicates that represent something that is seen as
 * true.
 */
public class Fact extends Tag {

    private factTypes factType;
    private int numValue;

    /**
     * Denotes the types a fact may take
     *
     * BOOL: A (is true)
     * CONST: A = 10
     * GT: A > 10
     * LT: A < 10
     */

    public enum factTypes {
        BOOL, CONST, GT, LT
    }

    /**
     * Creates a Fact.
     *
     * @param value the value of the Fact
     */

    public Fact(String value) {

        this.value = value;
        this.type = TagType.FACT;


        if (value.contains("="))
            this.factType = factTypes.CONST;
        else if (value.contains(">"))
            this.factType = factTypes.GT;
        else if (value.contains("<"))
            this.factType = factTypes.LT;
        else
            this.factType = factTypes.BOOL;


        if (this.factType == factTypes.BOOL)
            this.numValue = 1;
        else
            this.numValue = Integer.parseInt(value.replaceAll("[\\D]", ""));

    }

    /**
     * Returns a string with only name of the fact i.e. A=1 -> "A"
     * @return
     */

    public String toVariable() {
        return this.toString().replaceAll("[^A-Za-z]", "");
    }

    /**
     * Getter method for variable factType
     * @return factType of the object
     */

    public factTypes getFactType() {
        return this.factType;
    }

    /**
     * Getter method for variable numValue
     * @return integer corresponding to the numeric value of the object
     */

    public int getNumValue() {
        return this.numValue;
    }

    /**
     * Checks if two facts are true in the same system
     *
     * @param factSet the list of facts to iterate over for comparisons
     * @return true if facts match
     */

    public boolean matches(Set<Fact> factSet) {
        for (Fact that : factSet) {
            if ((this.getFactType() == factTypes.BOOL) || (that.getFactType() == factTypes.BOOL)) {
                return true;
            }
            if (this.getFactType() == factTypes.CONST) {
                if ((that.getFactType() == factTypes.CONST && this.getNumValue() == that.getNumValue()) ||
                    (that.getFactType() == factTypes.GT && this.getNumValue() > that.getNumValue()) ||
                        (that.getFactType() == factTypes.LT && this.getNumValue() < that.getNumValue()) ) {
                    return true;
                }
            }
            if (this.getFactType() == factTypes.GT) {
                if (that.getFactType() == factTypes.CONST && that.getNumValue() > this.getNumValue())
                    return true;
            }
            if (this.getFactType() == factTypes.LT) {
                if (that.getFactType() == factTypes.CONST && that.getNumValue() < this.getNumValue())
                    return true;
            }
        }
        return false;
    }

}
