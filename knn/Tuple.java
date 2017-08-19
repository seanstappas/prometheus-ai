package knn;

public class Tuple {
	public String s;
	public int value;
	
	public Tuple(String input, int inputValue){
		this.s = input;
		this.value = inputValue;
	}
	
	@Override
	public String toString(){
		String r = "(" + s + ", " + value + ")";
		return r;
	}
}
