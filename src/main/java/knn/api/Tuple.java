package knn.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class Tuple {
	public String s;
	public int value;
	
	/**
	 * Tuple is the format to mimic the output of Neural Network
	 * 
	 * @param input			the string value
	 * @param inputValue	the accuracy calculated from the Neural Network
	 */
	public Tuple(String input, int inputValue){
		this.s = input;
		this.value = inputValue;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("s", s)
				.append("value", value)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Tuple tuple = (Tuple) o;

		return new EqualsBuilder()
				.append(value, tuple.value)
				.append(s, tuple.s)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(s)
				.append(value)
				.toHashCode();
	}
}
