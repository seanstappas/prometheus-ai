package knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tags.*;

public class KnowledgeNodeNetwork {
	private HashMap<Tag, KnowledgeNode> mapKN;
	private HashMap<Tag, Double> inputTags;
    private HashMap<Tag, Double> activeTags;
    
    /**
     * constructor for testing only
     */
    public KnowledgeNodeNetwork(){
    	mapKN = new HashMap<>();
        activeTags = new HashMap<>();
        inputTags = new HashMap<>();
    }
    
    /**
     * Creates a new Knowledge Node Network (KNN) based on a database.
     *
     * @param dbFilename: the filename of the database to be read from (probably CSV or JSON)
     */
    public KnowledgeNodeNetwork(String dbFilename) {
        mapKN = new HashMap<>();
        activeTags = new HashMap<>();
        inputTags = new HashMap<>();
    }

    /**
     * Resets the KNN to a state from a database.
     *
     * @param dbFilename: the filename of the database to be read from
     */
    public void reset(String dbFilename) {

    }

    /**
     * Resets the KNN by clearing all data structures.
     */
    public void resetEmpty() {
        clearKN();
    }

    /**
     * Saves the current state of the KNN to a database.
     *
     * @param dbFilename: the filename of the database
     */
    public void saveKNN(String dbFilename) {

    }

    /**
     * Clears all the Knowledge Nodes from the KNN.
     */
    public void clearKN() {
        mapKN.clear();
        activeTags.clear();
    }

    /**
     * Adds a Knowledge Node to the KNN.
     *
     * @param kn: the Knowledge Node to be added
     */
    public void addKN(KnowledgeNode kn) {
        if(kn.type.equals(KnowledgeNode.inputType.FACT)){
        	this.mapKN.put(kn.fact, kn);
        }
        else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
        	this.mapKN.put(kn.recommendation, kn);
        }
        else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
        	this.mapKN.put(kn.rule, kn);
        }
    }

    /**
     * Deletes a Knowledge Node from the KNN.
     *
     * @param tag: the input Tag of the Knowledge Node to be deleted
     */
    public void delKN(Tag tag) {
        mapKN.remove(tag);
    }

    /**
     * Adds a fired Tag to the KNN.
     *
     * @param tag: the fired Tag to be added
     * @return true if the Tag is added successfully
     */
    public void addFiredTag(Tag tag, double objectTruth) {
    	this.activeTags.put(tag, objectTruth);
    }
    
    /**
     * Get access of input Tags
     * 
     * @return	the access of the input Tags found from the KNN using output from the neural network
     */
    public HashMap<Tag, Double> getInputTags(){
    	return this.inputTags;
    }
    
    /**
     * Get access of active Tags
     * 
     * @return the Access of active Tags
     */
    public HashMap<Tag, Double> getActiveTags() {
        return this.activeTags;
    }    
    
    public void lambdaSearch(ArrayList<Tuple> NNoutputs, String item){
    	HashMap<Tag, Double> bestPath = new HashMap<>();
    	double bestConfidence = 0;
    	
    	for(Tuple tp : NNoutputs){
    		boolean found = false;
    		for(KnowledgeNode kn : this.mapKN.values()){
    			if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    				if(kn.fact.getPredicateName().equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.fact, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.fact, kn.objectTruth);
    					this.activeTags.put(kn.fact, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.recommendation, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					this.activeTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.rule, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					this.activeTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.inputTags.put(rc, 0.0);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.inputTags.put(r, 0.0);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.inputTags.put(f, 0.0);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.inputTags.put(f, 0.0);
    			}
    		}
    	}
    	
    	for(Tag t : this.activeTags.keySet()){
    		HashSet<Tag> aboveTags = new HashSet<>();
    		aboveTags.add(t);
    		boolean added;
    		do{
    			added = false;
    			for(KnowledgeNode kn : this.mapKN.values()){
    				for(Tag tg : kn.outputs.keySet()){
    					if(aboveTags.contains(tg)){
    						Tag knType = kn.typeChecker();
    						if(aboveTags.contains(knType) == false){
    							aboveTags.add(knType);
    							added = true;
    							break;
    						}
    					}
    				}
    			}
    		}while(added == true);   		
    		
    		ArrayList<Tag> goodParents = new ArrayList<>();
    		ArrayList<Tag> notGoodParents = new ArrayList<>();   		
    		for(Tag parent : aboveTags){
    			boolean isGoodParent = false;
    			ArrayList<Tag> belowTags = new ArrayList<>();
    			depthFirstSearch(parent, belowTags);
    			for(Tag tg : belowTags){
    				if(tg.value.equals(item)){
    					isGoodParent = true;
    					goodParents.add(parent);
    					break;
    				}
    			}
    			if(isGoodParent == false){
    				notGoodParents.add(parent);
    			}
    		}
    		
    		for(Tag parent : goodParents){
    			ArrayList<Tag> belowTags = new ArrayList<>();
    			depthFirstSearch(parent, belowTags);
    			for(Tag tg : belowTags){
    				if(tg.value.equals(item)){
    					for(Tag tag : bestPath.keySet()){
    						if(tag.equals(t) == false){
    							this.mapKN.get(tag).objectTruth = 0;
    						}
    					}
    					ArrayList<Tag> parentToChild = pathFinder(parent, t);
    					ArrayList<Tag> parentToItem = pathFinder(parent, tg);
    					int numOfSame = 0;
    					for(Tag ptC : parentToChild){
    						if(ptC.equals(t) == false){
    							for(Tag p : aboveTags){
    								if(ptC.equals(p) && notGoodParents.contains(p) == false){
    									numOfSame++;
    									if(numOfSame > 1){
    			    						break;
    			    					}
    								}
    							}
    						}
    					}   					
						if(numOfSame > 1){
    						break;
    					}
    					
    					Collections.reverse(parentToChild);
    					backwardConfidence(parentToChild);
    					forwardConfidence(parentToItem);
    					if(this.mapKN.get(tg).objectTruth > bestConfidence){
    						bestPath.clear();
    						for(Tag tag : parentToChild){
    							bestPath.put(tag, this.mapKN.get(tag).objectTruth);
    						}
    						for(Tag tag : parentToItem){
    							bestPath.put(tag, this.mapKN.get(tag).objectTruth);
    						}
    						bestConfidence = this.mapKN.get(tg).objectTruth;
    					}   					
    				}
    			}
    		}
    	}

    	this.activeTags.putAll(bestPath);
    } 
    
    /**
     * Calculate the total confidence of a given path
     * 
     * @param path: the path needed to calculate the total confidence
     * @return the total confidence of that path
     */
    public double forwardConfidence(ArrayList<Tag> path){
    	double totalConfidence = 100;
    	if(path.size() == 1){
    		return this.mapKN.get(path.get(0)).objectTruth;
    	}
    	
    	ArrayList<Double> listOfObjectTruth = new ArrayList<>();
    	for(int i=0; i<path.size()-1; i++){
    		Tag current = path.get(i);
    		Tag next = path.get(i+1);
    		listOfObjectTruth.add(this.mapKN.get(current).objectTruth);
    		this.mapKN.get(next).objectTruth = this.mapKN.get(current).objectTruth * this.mapKN.get(current).outputs.get(next)/100;
    	}
    	listOfObjectTruth.add(this.mapKN.get(path.get(path.size()-1)).objectTruth);
    	
    	for(int i=0; i<listOfObjectTruth.size(); i++){
    		totalConfidence = (totalConfidence * listOfObjectTruth.get(i)) /100;
    	}
    	
    	return totalConfidence;
    }
    
    public double backwardConfidence(ArrayList<Tag> path){
    	double totalConfidence = 100;
    	if(path.size() == 1){
    		return this.mapKN.get(path.get(0)).objectTruth;
    	}
    	
    	ArrayList<Double> listOfObjectTruth = new ArrayList<>();
    	for(int i=0; i<path.size()-1; i++){
    		Tag current = path.get(i);
    		Tag next = path.get(i+1);
    		listOfObjectTruth.add(this.mapKN.get(current).objectTruth);
    		this.mapKN.get(next).listOfRelatedTruth.put(current, (this.mapKN.get(current).objectTruth * this.mapKN.get(next).outputs.get(current) /100) );
    		this.mapKN.get(next).updateObjectConfidence();
    	}
    	listOfObjectTruth.add(this.mapKN.get(path.get(path.size()-1)).objectTruth);
    	
    	for(int i=0; i<listOfObjectTruth.size(); i++){
    		totalConfidence = (totalConfidence * listOfObjectTruth.get(i)) /100;
    	}
    	
    	return totalConfidence;
    }
    
    /**
     * Find all the tags, a path, between two given tag
     * 
     * @param start: the given tag to start the path
     * @param end: the given tag to finish the path
     * @return an arraylist that stores all the tags of the found path. The first slot of the list is the starting tag and the last slot is the ending tag
     */
    public ArrayList<Tag> pathFinder(Tag start, Tag end){
    	ArrayList<Tag> currentPath = new ArrayList<>();
    	currentPath.add(start);
    	if(this.mapKN.get(start).outputs.containsKey(end)){
    		currentPath.add(end);
    		return currentPath;
    	}
    	else{
    		for(Tag t : this.mapKN.get(start).outputs.keySet()){
    			ArrayList<Tag> template = pathFinder(t, end);
    			if(template.get(template.size()-1).equals(end)){
    				currentPath.addAll(template);
    				break;
    			}
    		}
    	}
    	return currentPath;
    }
    
    /**
     * Depth first search (DFS) on a specific tag
     * 
     * @param tag: the tag to start the DFS
     * @param list: to store all the tag found during DFS
     */   
    public void depthFirstSearch(Tag tag, ArrayList<Tag> list){
    	list.add(tag);
    	for(Tag t : this.mapKN.get(tag).outputs.keySet()){
    		if(list.contains(t) == false){
    			depthFirstSearch(t, list);
    		}
    	}
    }
    
    
    /**
     * Backward search with ply as input
     * 
     * @param score: indication of accuracy 
     * @param confidenceLeve: only tags that have at least certain confidence value count as a matching
     * @param ply: number of cycle the AI wanted to search
     */
    public void backwardSearch(ArrayList<Tuple> NNoutputs, double score, int ply){
    	for(Tuple tp : NNoutputs){
    		boolean found = false;
    		for(KnowledgeNode kn : this.mapKN.values()){
    			if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    				if(kn.fact.getPredicateName().equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.fact, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.fact, kn.objectTruth);
    					this.activeTags.put(kn.fact, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.recommendation, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					this.activeTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.rule, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					this.activeTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.inputTags.put(rc, 0.0);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.inputTags.put(r, 0.0);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.inputTags.put(f, 0.0);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.inputTags.put(f, 0.0);
    			}
    		}
    	}
    	
    	for(int i=0; i<ply; i++){
    		ArrayList<Tag> previousActiveList = new ArrayList<>();
    		for(Tag t : this.activeTags.keySet()){
    			previousActiveList.add(t);
    		}
    		
    		for(KnowledgeNode kn : this.mapKN.values()){
    			int matching = 0;
    			for(Tag t : kn.outputs.keySet()){
    				if(previousActiveList.contains(t)){
    					matching++;
    					double backwardConfidence = ( kn.outputs.get(t)*this.mapKN.get(t).objectTruth )/100;
    					kn.listOfRelatedTruth.put(t, backwardConfidence);
    				}
    			}
    			if( (double)matching/kn.outputs.size() >= score ){
    				Tag knType = kn.typeChecker();
    				kn.updateObjectConfidence();
    				if(this.activeTags.containsKey(knType) == false){    					
    					this.activeTags.put(knType, kn.objectTruth);
    				}
    			}
    		}
    	}
    	
    }
    
    /**
     * Backward searching with unlimited time
     * 
     * @param score: an indication of accuracy and it is calculated by the AI
     * @param confidenceLeve: only tags that have at least certain confidence (between 1 to 100) value count as a matching
     * If the number of matching between active Tags and the outputs of a kn, then the Tag of that kn is added to the active list
     */
    public void backwardSearch(ArrayList<Tuple> NNoutputs, double score){
    	for(Tuple tp : NNoutputs){
    		boolean found = false;
    		for(KnowledgeNode kn : this.mapKN.values()){
    			if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    				if(kn.fact.getPredicateName().equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.fact, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.fact, kn.objectTruth);
    					this.activeTags.put(kn.fact, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.recommendation, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					this.activeTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.rule, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					this.activeTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.inputTags.put(rc, 0.0);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.inputTags.put(r, 0.0);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.inputTags.put(f, 0.0);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.inputTags.put(f, 0.0);
    			}
    		}
    	}
    	
    	HashMap<Tag, Double> pendingFacts = new HashMap<>();
    	do{
    		pendingFacts.clear();
    		for(KnowledgeNode kn : this.mapKN.values()){
    			int matching = 0;
    			for(Tag t : kn.outputs.keySet()){
    				if(this.activeTags.containsKey(t)){
    					matching++;
    					double backwardConfidence = (kn.outputs.get(t) * this.mapKN.get(t).objectTruth) /100;
    					kn.listOfRelatedTruth.put(t, backwardConfidence);
    				}
    			}
    			if( (double)matching/kn.outputs.size() >= score ){
    				Tag knType = kn.typeChecker();
    				kn.updateObjectConfidence();
    				if(this.activeTags.containsKey(knType) == false){
    					pendingFacts.put(knType, kn.objectTruth);
    				}    				
    			}
    		}
    		this.activeTags.putAll(pendingFacts);;
    	}while(pendingFacts.isEmpty() == false);
    }
    
    /**
     * Forward searching with ply as number of depth 
     * 
     * @param NNoutputs: a HashMap mimic the output from the neural network, a list of tuple of form (String, value)
     * @param ply: number of time of searching in the knowledge node network
     */    
    public void forwardSearch(ArrayList<Tuple> NNoutputs, int ply){
    	for(Tuple tp : NNoutputs){
    		boolean found = false;
    		for(KnowledgeNode kn : this.mapKN.values()){
    			if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    				if(kn.fact.getPredicateName().equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.fact, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.inputTags.put(rc, 0.0);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.inputTags.put(r, 0.0);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.inputTags.put(f, 0.0);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.inputTags.put(f, 0.0);
    			}
    		}
    	}

    	if(ply > 0 && this.activeTags.isEmpty() == false){
    		for(int i=0; i<ply; i++){
    			ArrayList<Tag> activeList = new ArrayList<>();      			
       			for(Tag t : this.activeTags.keySet()){
       				activeList.add(t);
       			}
       			
    			for(Tag t : activeList){
    				if(this.mapKN.containsKey(t)){
    					if(this.mapKN.get(t).isFired != true && this.mapKN.get(t).activation >= this.mapKN.get(t).threshold){
    						excite(this.mapKN.get(t), 0);
    					}
    				}
    			}
    		}
    	}
    }
    
    
    /**
     * forwardSearch with unlimited time
     * 
     * @param NNoutputs: a HashMap mimic the output from the neural network, a list of tuple of form (String, value)
     * If a string match a knowledge node in the knowledge node network, that knowledge node will be excite 
     */  
    public void forwardSearch(ArrayList<Tuple> NNoutputs){
    	for(Tuple tp : NNoutputs){
    		boolean found = false;
    		for(KnowledgeNode kn : this.mapKN.values()){
    			if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    				if(kn.fact.getPredicateName().equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.fact, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.inputTags.put(rc, 0.0);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.inputTags.put(r, 0.0);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.inputTags.put(f, 0.0);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.inputTags.put(f, 0.0);
    			}
    		}
    	}
    	
    	boolean allActived;
   		do{
   			allActived = true;
   			ArrayList<Tag> activeList = new ArrayList<>();
   			
   			for(Tag t : this.activeTags.keySet()){
   				activeList.add(t);
   			}
   			
   			for(Tag t : activeList){
   				if(this.mapKN.containsKey(t)){
   					if(this.mapKN.get(t).isFired != true && this.mapKN.get(t).activation >= this.mapKN.get(t).threshold){
   						excite(this.mapKN.get(t), 0);
   						allActived = false;
   					}
   				}
    		}		
    	}while(allActived == false);
    }    
 
    
    /**
     * Excites a Knowledge Node. 
     *
     * @param kn: the Knowledge Node to excite
     * @param value: the accuracy from the neural network
     * If excitation leads to firing, this will add the fired kn to the activeTag.
     */    
    public void excite(KnowledgeNode kn, int value) {
    	kn.increaseActivation(value);
    	if(kn.activation * kn.strength >= kn.threshold){
    		Tag ownTag = kn.typeChecker();
    		if(value != 0){
    			kn.listOfRelatedTruth.put(ownTag, kn.accuracy[value]);
    			kn.updateObjectConfidence();
    			this.activeTags.put(ownTag, kn.objectTruth);
    		}
    		kn.isActivated = true;   		
    		fire(kn);
    		kn.isFired = true;
    		for(Tag t : kn.outputs.keySet()){
    			updateConfidence(this.mapKN.get(t));
    		}
    	}
    }    
    
    /**
     * Fires a Knowledge Node.
     * 
     * @param kn: Knowledge Node to fire
     * @return a Set of Tags found from the output list of kn that have activation >= threshold
     */
    public void fire(KnowledgeNode kn) {
        for (Tag t : kn.outputs.keySet()) {
        	KnowledgeNode currentKN = this.mapKN.get(t);
        	currentKN.activation+=100;
        	if(currentKN.activation >= currentKN.threshold){
        		Tag parentTag = kn.typeChecker();        			
        		currentKN.listOfRelatedTruth.put( parentTag, (kn.objectTruth*kn.outputs.get(t))/100 );        		
        		currentKN.updateObjectConfidence();
        		currentKN.isActivated = true;
        		this.activeTags.put(t, currentKN.objectTruth);
        	}
        }
    }
    
    /**
     * 
     * @param kn
     */
    public void updateConfidence(KnowledgeNode kn){
    	for(Tag t : kn.outputs.keySet()) {
    		KnowledgeNode currentKN = this.mapKN.get(t);
    		if(currentKN.isActivated == true){
    			Tag parentTag = kn.typeChecker();        			
        		currentKN.listOfRelatedTruth.put( parentTag, (kn.objectTruth*kn.outputs.get(t))/100 );       		
        		currentKN.updateObjectConfidence();
        		this.activeTags.put(t, currentKN.objectTruth);
        		updateConfidence(currentKN);
    		}
    	}
    }

}
