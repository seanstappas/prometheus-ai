package knn;

public class Tuple {
	public String s;
	public int value;
	
	/**
	 * Tuple is the format to mimic the output of Neural Network
	 * 
	 * @param input			the string value
	 * @param inputValue	the accurarcy calculated from the Neural Network
	 */
	public Tuple(String input, int inputValue){
		this.s = input;
		this.value = inputValue;
	}
	
	@Override
	public String toString(){
		return "(" + s + ", " + value + ")";
	}
}
