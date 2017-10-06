package knn;

import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

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
     * @param dbFilename the filename of the database to be read from (probably CSV or JSON)
     */
    public KnowledgeNodeNetwork(String dbFilename) {
        mapKN = new HashMap<>();
        activeTags = new HashMap<>();
        inputTags = new HashMap<>();
    }

    /**
     * Resets the KNN to a state from a database.
     *
     * @param dbFilename the filename of the database to be read from
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
     * @param dbFilename the filename of the database
     */
    public void saveKNN(String dbFilename) {

    }

    /**
     * Clears all the Knowledge Nodes from the KNN.
     */
    public void clearKN() {
        mapKN.clear();
        activeTags.clear();
        inputTags.clear();
    }

    /**
     * Adds a Knowledge Node to the KNN.
     *
     * @param kn the Knowledge Node to be added
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
     * @param tag the input Tag of the Knowledge Node to be deleted
     */
    public void delKN(Tag tag) {
    	mapKN.remove(tag);
    }

    /**
     * Adds a fired Tag to the KNN.
     *
     * @param tag the fired Tag to be added
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
     * @return	the Access of active Tags
     */
    public HashMap<Tag, Double> getActiveTags() {
		return this.activeTags;
    }    
    
    /**
     * Lambda search, a search to find out the best relation between a know list of tags and a wanted item tag
     * 
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param item the wanted item tag
     */
    public void lambdaSearch(ArrayList<Tuple> NNoutputs, Tag item){
    	HashMap<Tag, Double> bestPath = new HashMap<>();
    	double bestConfidence = 0;

    	//Convert information from the NN to tags
    	getInputForBackwardSearch(NNoutputs);
    	
    	for(Tag startPoint : this.activeTags.keySet()){
    		HashSet<Tag> allParentsofStartPoint = findAllParentKNofGivenTag(startPoint);

    		//Find parents of the starting point that either has a path to the item or not
    		ArrayList<Tag> tagHavePathToItem = new ArrayList<>();
    		for(Tag parent : allParentsofStartPoint){
    			ArrayList<Tag> belowTags = new ArrayList<>();
    			depthFirstSearch(parent, belowTags);
    			for(Tag tg : belowTags){
    				if(tg.equals(item)){
    					tagHavePathToItem.add(parent);
    					break;
    				}
    			}
    		}

    		//For each parent of the startPoint, find if there is a non cycle path that connect both the startPoint and the Item tag
    		for(Tag parentOfStartPoint : tagHavePathToItem){
    			ArrayList<Tag> descendants = new ArrayList<>();
    			depthFirstSearch(parentOfStartPoint, descendants);
    			for(Tag tg : descendants){
    				if(tg.equals(item)){
    					reset_ObjectTruth_Of_Tags_In_A_Path(startPoint, bestPath); //ObjectTruth of Tag t should not be reset because it is the starting point to calculate a new path
    					ArrayList<Tag> bads = new ArrayList<>();    					
    					ArrayList<Tag> parentToItem = pathFinder(parentOfStartPoint, tg, bads);
    					ArrayList<Tag> parentToChild;
    					ArrayList<Tag> previousParentToChild;
    					int numOfSame;

    					//find if there is any tag beside the startPoint and the parentOfStartPoint from the parentToChild is also belong to parentToItem
						//if it does, then try to find another path for parentToChild until there is no more possible path.
    					do{
    						parentToChild = pathFinder(parentOfStartPoint, startPoint, bads);
    						numOfSame = 0;
    						for (Tag tagInParentToChild : parentToChild){
    							if (!tagInParentToChild.equals(startPoint) && !tagInParentToChild.equals(parentOfStartPoint)){
    								if(parentToItem.contains(tagInParentToChild)){
    									bads.add(tagInParentToChild);
    									numOfSame++;
    									break;
									}
								}
							}
    						previousParentToChild = pathFinder(parentOfStartPoint, startPoint, bads);
    					}while(numOfSame > 0 && !parentToChild.equals(previousParentToChild));

    					//If all the possible for parentToChild has a tag that is also belong to the path of parentToItem, then this parent is will create a path containing a cycle within, therefore we need to skip
						if(numOfSame > 0){
    						break;
    					}

    					//Calculate the new objectTruth of the Item tag based on the path it just found and store it in the KNN
    					Collections.reverse(parentToChild); //parentToChild become childToParent
    					backwardConfidence(parentToChild);
    					forwardConfidence(parentToItem);

    					//If the calculated objectTruth of the Item tag is larger than the previous found maximum objectTruth of the Item tag, it updates the best path
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

    	//add all the tag from the best path to the activeTags list of the KNN
    	this.activeTags.putAll(bestPath);
    }

	/**
	 *
	 * @param start the tag that start the path
	 * @param path path that contains tags needed to be reset their objectTruth value
	 */
	public void reset_ObjectTruth_Of_Tags_In_A_Path(Tag start, HashMap<Tag, Double> path){
		for(Tag tag : path.keySet()){
			if(!tag.equals(start)){
				this.mapKN.get(tag).objectTruth = 0;
			}
		}
	}

	/**
	 *
	 * @param t the wanted tag to find its parents
	 * @return a HashSet of tags that has a path to the wanted tag t
	 */
    public HashSet<Tag> findAllParentKNofGivenTag(Tag t){
	   	HashSet<Tag> allParents = new HashSet<>();
	   	allParents.add(t);
		boolean added;
		do{
			added = false;
			for(KnowledgeNode kn : this.mapKN.values()){
				for(Tag tg : kn.outputs.keySet()){
					if(allParents.contains(tg)){
						Tag knType = kn.typeChecker();
						if(!allParents.contains(knType)){
							allParents.add(knType);
							added = true;
						}
					}
				}
			}
		}while(added);

		return allParents;
	}
    
    /**
     * Calculate the confidence of each KN starting from its parental KN (one of the KN in its output list) in a path.
     * 
     * @param path a path of KN with descending order, parental to child KN
     */
    public void forwardConfidence(ArrayList<Tag> path){
    	double totalConfidence = 100;
    	if(path.size() != 1){
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
    	}    	    	
    }
    
    /**
     * Calculate the confidence of each KN starting from its child KN (one of the KN in its output list) in a path.
     * 
     * @param path a path of KN with ascending order, child to parental KN
     */
    public void backwardConfidence(ArrayList<Tag> path){
    	double totalConfidence = 100;
    	if(path.size() != 1){
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
    	}
    	
    }
    
    /**
     * Find a path between two given tag and all the tag involved in that path
     * 
     * @param start the given tag to start the path
     * @param end the given tag to finish the path
	 * @param badComponents tags that can have a down stream path to both the start and end point
     * @return	an ArrayList that stores all the tags of the found path. The first slot of the list is the starting tag and the last slot is the ending tag
     */
    public ArrayList<Tag> pathFinder(Tag start, Tag end, ArrayList<Tag> badComponents){
    	ArrayList<Tag> currentPath = new ArrayList<>();
    	currentPath.add(start);
    	if(this.mapKN.get(start).outputs.containsKey(end) && !badComponents.contains(start)){
    		currentPath.add(end);
    		return currentPath;
    	}
    	else{
    		for(Tag t : this.mapKN.get(start).outputs.keySet()){
    			ArrayList<Tag> template = pathFinder(t, end, badComponents);
    			if(template.get(template.size()-1).equals(end) && !badComponents.contains(start)){
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
     * @param tag the tag to start the DFS
     * @param list to store all the tag found during DFS
     */   
    public void depthFirstSearch(Tag tag, ArrayList<Tag> list){
    	list.add(tag);
    	for(Tag t : this.mapKN.get(tag).outputs.keySet()){
    		if(!list.contains(t)){
    			depthFirstSearch(t, list);
    		}
    	}
    }
    
    
    /**
     * Backward search with ply as input
     * 
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param score indication of accuracy
     * @param ply number of cycle the AI wanted to search
     */
    public void backwardSearch(ArrayList<Tuple> NNoutputs, double score, int ply){
    	getInputForBackwardSearch(NNoutputs);
    	
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
    				if(!this.activeTags.containsKey(knType)){
    					this.activeTags.put(knType, kn.objectTruth);
    				}
    			}
    		}
    	}
    	
    }
    
    /**
     * Backward searching with unlimited time
     * 
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param score the minimum number of matching needed from the output list of a KN in order for that KN to become active.
     */
    public void backwardSearch(ArrayList<Tuple> NNoutputs, double score){
    	getInputForBackwardSearch(NNoutputs);
    	
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
    				if(!this.activeTags.containsKey(knType)){
    					pendingFacts.put(knType, kn.objectTruth);
    				}    				
    			}
    		}
			this.activeTags.putAll(pendingFacts);
		} while (!pendingFacts.isEmpty());
	}
    
    /**
     * Creating input Tags from string in the output of Neural Network (NN)
     * This method is used only for backward or lambda search because no excitation is needed during the Tag creation
     * 
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     */
    public void getInputForBackwardSearch(ArrayList<Tuple> NNoutputs){
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
    				if(kn.recommendation.getPredicateName().equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.recommendation, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					this.activeTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.toString().equals(tp.s)){
    					kn.listOfRelatedTruth.put(kn.rule, kn.accuracy[tp.value]);
    					kn.updateObjectConfidence();
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					this.activeTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(!found){
				createKNfromTuple(tp);
    		}
    	}
    }
    
    /**
     * Forward searching with ply as number of depth 
     * 
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     * @param ply number of time of searching in the knowledge node network
     */    
    public void forwardSearch(ArrayList<Tuple> NNoutputs, int ply){
    	getInputForForwardSearch(NNoutputs);

    	if(ply > 0 && !this.activeTags.isEmpty()){
    		for(int i=0; i<ply; i++){
    			ArrayList<Tag> activeList = new ArrayList<>();      			
       			for(Tag t : this.activeTags.keySet()){
       				activeList.add(t);
       			}
       			
    			for(Tag t : activeList){
    				if(this.mapKN.containsKey(t)){
    					if(!this.mapKN.get(t).isFired && this.mapKN.get(t).activation >= this.mapKN.get(t).threshold){
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
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     */  
    public void forwardSearch(ArrayList<Tuple> NNoutputs){
    	getInputForForwardSearch(NNoutputs);
    	
    	boolean allActived;
   		do{
   			allActived = true;
   			ArrayList<Tag> activeList = new ArrayList<>();
   			
   			for(Tag t : this.activeTags.keySet()){
   				activeList.add(t);
   			}
   			
   			for(Tag t : activeList){
   				if(this.mapKN.containsKey(t)){
   					if(!this.mapKN.get(t).isFired && this.mapKN.get(t).activation >= this.mapKN.get(t).threshold){
   						excite(this.mapKN.get(t), 0);
   						allActived = false;
   					}
   				}
    		}		
    	}while(!allActived);
    }    
    
    /**
     * Creating input Tags from string in the output of Neural Network (NN)
     * This method is used only for forward search because excitation may be active during the Tag creation
     * 
     * @param NNoutputs a list of tuple of form (String, value) to mimic the output of Neural Network
     */
    public void getInputForForwardSearch(ArrayList<Tuple> NNoutputs){
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
    				if(kn.recommendation.getPredicateName().equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.recommendation, kn.objectTruth);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.toString().equals(tp.s)){
    					excite(kn, tp.value);
    					this.inputTags.put(kn.rule, kn.objectTruth);
    					found = true;
    				}
    			}
    		}
    		if(!found){
				createKNfromTuple(tp);
    		}
    	}
    }

	/**
	 * Create a KN from a Tuple in KNN
	 *
	 * @param tp tuple used to create the KN
	 */
	public void createKNfromTuple(Tuple tp){
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
    
    /**
     * Excites a Knowledge Node. 
     *
     * @param kn the Knowledge Node to excite
     * @param value the accuracy from the neural network
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
     * @param kn Knowledge Node to fire
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
     * Update the confidence of those active KN found in output list of a KN with its latest confidence value
     * 
     * @param kn the kn that has a new confidence value
     */
    public void updateConfidence(KnowledgeNode kn){
    	for(Tag t : kn.outputs.keySet()) {
    		KnowledgeNode currentKN = this.mapKN.get(t);
    		if(currentKN.isActivated){
    			Tag parentTag = kn.typeChecker();        			
        		currentKN.listOfRelatedTruth.put( parentTag, (kn.objectTruth*kn.outputs.get(t))/100 );       		
        		currentKN.updateObjectConfidence();
        		this.activeTags.put(t, currentKN.objectTruth);
        		updateConfidence(currentKN);
    		}
    	}
    }
}
