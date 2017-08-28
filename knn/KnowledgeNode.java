package knn;

import java.util.HashMap;

import tags.*;

public class KnowledgeNode {
	inputType type;
	enum inputType{FACT, RECOMMENDATION, RULE}
	Fact fact;
	Rule rule;
	Recommendation recommendation;
	HashMap<Tag, Integer> outputs;	// Integer is the value of confidence 
	int activation = 0;			    // int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    int threshold; 		 	// limit: When activation > threshold : fires output tags (outputTags array). These tags can be lists of rules or facts.
    double age = System.currentTimeMillis() / 1000L; 			// Age timestamp. Set to current UNIX time when node is newly formed.
    int strength = 1;			// Which strength approach to take?
    double maxAge = 60;
    boolean isActivated = false;
    int[] accuracy = {0, 2, 5, 11, 27, 50, 73, 88, 95, 98, 100};	//sigmoid function activation value
    
    public KnowledgeNode(Tag inputName, HashMap<Tag, Integer> outputTags, int threshold) {
        if(inputName.type.equals(Tag.TagType.FACT)){
        	this.type = inputType.FACT;
        	this.fact = (Fact) inputName;
        }
        else if(inputName.type.equals(Tag.TagType.RECOMMENDATION)){
        	this.type = inputType.RECOMMENDATION;
        	this.recommendation = (Recommendation) inputName;
        }
        else if(inputName.type.equals(Tag.TagType.RULE)){
        	this.type = inputType.RULE;
        	this.rule = (Rule) inputName;
        }
        this.outputs = outputTags;
        this.threshold = threshold;
    }
    
    /**
     * Full constructor
     *
     * @param inputTag    the input tag of the Knowledge Node
     * @param outputTags  the output Tag of the Knowledge Node
     * @param threshold   the threshold of activation
     * @param strength    the strength value to bias activation
     * @param type        the type of the Knowledge Node (linear or sigmoid activation)
     * @param maxAge      threshold age for the node to be discarded
     */
    public KnowledgeNode(Tag inputName, HashMap<Tag, Integer> outputTags, int threshold, int strength, double maxAge) {
    	if(inputName.type.equals(Tag.TagType.FACT)){
        	this.type = inputType.FACT;
        	this.fact = (Fact) inputName;
        }
        else if(inputName.type.equals(Tag.TagType.RECOMMENDATION)){
        	this.type = inputType.RECOMMENDATION;
        	this.recommendation = (Recommendation) inputName;
        }
        else if(inputName.type.equals(Tag.TagType.RULE)){
        	this.type = inputType.RULE;
        	this.rule = (Rule) inputName;
        }
        this.outputs = outputTags;
        this.threshold = threshold;
        this.outputs = outputTags;
        this.threshold = threshold;
        this.strength = strength;
        this.maxAge = maxAge;
    }
    
    /**
     * Creates a Knowledge Node from Strings. Assumes all Tags are of the provided TagType.
     *
     * @param inputTag    the input Tag of the Knowledge Node
     * @param outputTags  the output Tag of the Knowledge Node
     * @param tagType     the type of all Tags (input and output)
     */
    public KnowledgeNode(String[] inputInfo) {
        this.outputs = new HashMap<Tag, Integer>();
        if(inputInfo[0].charAt(0) == '@'){
        	this.type = inputType.RECOMMENDATION;
        	this.recommendation = new Recommendation(inputInfo[0]);
        }
        else if(inputInfo[0].contains("->")){
        	this.type = inputType.RULE;
        	this.rule = new Rule(inputInfo[0]);
        }
        else if(inputInfo[0].matches(".*\\(.*\\).*")){
        	this.type = inputType.FACT;
        	this.fact = new Fact(inputInfo[0]);
        }
        this.threshold = Integer.parseInt(inputInfo[1]);
        
        for(int i=2; i<inputInfo.length; i+=2){
        	if(inputInfo[i].charAt(0) == '@'){
        		Recommendation rcmd = new Recommendation(inputInfo[i]);
        		this.outputs.put(rcmd, Integer.parseInt(inputInfo[i+1]));
        	}
        	else if(inputInfo[i].contains("->")){
        		Rule r = new Rule(inputInfo[i]);
        		this.outputs.put(r,Integer.parseInt(inputInfo[i+1]));
        	}
        	else if(inputInfo[i].matches(".*\\(.*\\).*")){
        		Fact f = new Fact(inputInfo[i]);
        		this.outputs.put(f, Integer.parseInt(inputInfo[i+1]));
        	}
        }
    }

    /**
     * Ages the current Knowledge Node.
     * @return the age (time elapsed since initialisation/last update)
     */
    public double updateAge() {
        this.age = (System.currentTimeMillis() / 1000L) - this.age;
        return this.age;
    }

    @Override
    public String toString() {
    	String result = "";
        if(this.type.equals(inputType.RECOMMENDATION)){
        	result += this.recommendation.toString();
        	result += " threshold is " + this.threshold;
        	return result;
        }
        else if(this.type.equals(inputType.RULE)){
        	result += this.rule.toString();
        	result += " threshold is " + this.threshold;
        	return result;
        }
        
        result += this.fact.toString();
        result = result + " threshold is " + this.threshold + " => ";
        
        for(Tag t : this.outputs.keySet()){
        	result += t.toString();
        	result = result + " with confidence=" + this.outputs.get(t) + "; ";
        }
        
        return result;
    }

    public void increaseActivation() {
        this.activation++;
    }
    
    public void increaseActivation(int value) {
    	this.activation = this.activation + this.accuracy[value];
    }

    public int getActivation() {
        return activation;
    }

}
