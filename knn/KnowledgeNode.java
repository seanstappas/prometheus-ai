package knn;

import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.HashMap;

public class KnowledgeNode {
	inputType type;
	enum inputType{FACT, RECOMMENDATION, RULE}
	Fact fact;
	Rule rule;
	Recommendation recommendation;
	HashMap<Tag, Double> outputs;										// Integer is the value of confidence 
	double activation = 0;			    									// int starts at 0 goes to 1 (can be sigmoid, or jump to 1). Increases when sees tag.
    double threshold;                                                        // limit: When activation > threshold : fires output tags (outputFacts array). These tags can be lists of rules or facts.
    double objectTruth = 0;
    HashMap<Tag, Double> listOfRelatedTruth;
    double age = System.currentTimeMillis() / 1000L; 					// Age timestamp. Set to current UNIX time when node is newly formed.
    int strength = 1;													// Which strength approach to take?
    double maxAge = 60;
    boolean isActivated = false;
    boolean isFired = false;
    double[] accuracy = {0, 2, 5, 11, 27, 50, 73, 88, 95, 98, 100};		//sigmoid function activation value

    public KnowledgeNode(Tag inputName, HashMap<Tag, Double> outputFacts, int threshold) {
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
        this.outputs = outputFacts;
        this.threshold = threshold;
        this.listOfRelatedTruth = new HashMap<>();
    }
    
    /**
     * Full constructor
     *
     * @param inputName    the input tag of the Knowledge Node
     * @param outputFacts  the output Tag of the Knowledge Node
     * @param threshold   the threshold of activation
     * @param strength    the strength value to bias activation
     * @param maxAge      threshold age for the node to be discarded
     */
    public KnowledgeNode(Tag inputName, HashMap<Tag, Double> outputFacts, int threshold, int strength, double maxAge) {
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
        this.outputs = outputFacts;
        this.threshold = threshold;
        this.outputs = outputFacts;
        this.threshold = threshold;
        this.strength = strength;
        this.maxAge = maxAge;
        this.listOfRelatedTruth = new HashMap<>();
    }
    
    /**
     * Creates a Knowledge Node from Strings.
     *
     * @param inputInfo    The info String to create the Knowledge Node
     */
    public KnowledgeNode(String[] inputInfo) {
    	this.listOfRelatedTruth = new HashMap<>();
        this.outputs = new HashMap<Tag, Double>();
        
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
        		this.outputs.put(rcmd, Double.parseDouble(inputInfo[i+1]));
        	}
        	else if(inputInfo[i].contains("->")){
        		Rule r = new Rule(inputInfo[i]);
        		this.outputs.put(r, Double.parseDouble(inputInfo[i+1]));
        	}
        	else if(inputInfo[i].matches(".*\\(.*\\).*")){
        		Fact f = new Fact(inputInfo[i]);
        		this.outputs.put(f, Double.parseDouble(inputInfo[i+1]));
        	}
        }
    }
    
    /**
     * Check what type of Tag this knowledge node has
     * 
     * @return the corresponding tag of the KN
     */
    public Tag typeChecker(){
    	if(this.type.equals(KnowledgeNode.inputType.FACT)){
			Tag t = this.fact;
			return t;
		}
		else if(this.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
			Tag t = this.recommendation;
			return t;
		}
		else{
			Tag t = this.rule;
			return t;
		}     	
    }
    
    /**
     * update object confidence value
     */
    public void updateObjectConfidence(){
    	double sum=0;
    	for(Tag t : this.listOfRelatedTruth.keySet()){
    		sum = sum + this.listOfRelatedTruth.get(t);
    	}
    	this.objectTruth = sum / this.listOfRelatedTruth.size();
    }
    
    /**
     * Ages the current Knowledge Node.
     * 
     * @return the age (time elapsed since initialisation/last update)
     */
    public double updateAge() {
        this.age = (System.currentTimeMillis() / 1000L) - this.age;
        return this.age;
    }

    public void setObjectTruth(double value){
        this.objectTruth = value;
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
        else{
            result += this.fact.toString();
            result = result + " threshold is " + this.threshold + " => ";
        }
        
        for(Tag t : this.outputs.keySet()){
        	result += t.toString();
        	result = result + " with membershipTruth=" + this.outputs.get(t) + "; ";
        }
        
        return result;
    }

    public void increaseActivation() {
        this.activation++;
    }
    
    public void increaseActivation(int value) {
    	this.activation = this.activation + this.accuracy[value];
    }

    public double getActivation() {
        return activation;
    }

}
