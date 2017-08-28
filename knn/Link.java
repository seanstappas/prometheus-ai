package knn;

import tags.Tag;

public class Link {
	private Tag parent;
	private Tag child;
	private int confidence;
	
	public Link(Tag p, int c, Tag ch){
		this.parent = p;
		this.confidence = c;
		this.child = ch;
	}
	
	public String toString(){
		return "(" + this.parent.value + ", " + this.confidence + ", " + this.child.value + ")";
	}
}
