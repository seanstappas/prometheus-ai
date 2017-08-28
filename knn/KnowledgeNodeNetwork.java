package knn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tags.*;

public class KnowledgeNodeNetwork {
	private HashMap<Tag, KnowledgeNode> mapKN;
    private HashSet<Tag> activeTags;
    private HashSet<Tag> treeRoots;
    private HashSet<Link> links;
    
    /**
     * constructor for testing only
     */
    public KnowledgeNodeNetwork(){
    	mapKN = new HashMap<>();
        activeTags = new HashSet<>();
        treeRoots = new HashSet<>();
        links = new HashSet<>();
    }
    
    /**
     * Creates a new Knowledge Node Network (KNN) based on a database.
     *
     * @param dbFilename: the filename of the database to be read from (probably CSV or JSON)
     */
    public KnowledgeNodeNetwork(String dbFilename) {
        mapKN = new HashMap<>();
        activeTags = new HashSet<>();
        treeRoots = new HashSet<>();
        links = new HashSet<>();
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
    public boolean addFiredTag(Tag tag) {
    	return activeTags.add(tag);
    }
    
    /**
     * Get access of active Tags
     * 
     * @return the Access of active Tags
     */
    public HashSet<Tag> getActiveTags() {
        return this.activeTags;
    }    
    
    public void lambdaSearch(String item){
    	ArrayList<Tag> bestPath = new ArrayList<>();
    	double bestConfidence = 0;
    	
    	for(Tag t : this.activeTags){
    		HashSet<Tag> aboveTags = new HashSet<>();
    		aboveTags.add(t);
    		boolean added;
    		do{
    			added = false;
    			for(KnowledgeNode kn : this.mapKN.values()){
    				for(Tag tg : kn.outputs.keySet()){
    					if(aboveTags.contains(tg)){
    						if(kn.type.equals(KnowledgeNode.inputType.FACT) && aboveTags.contains(kn.fact) == false){
    							aboveTags.add(kn.fact);
    							added = true;
    							break;
    						}
    						else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION) && aboveTags.contains(kn.recommendation) == false){
    							aboveTags.add(kn.recommendation);
    							added = true;
    							break;
    						}
    						else if(kn.type.equals(KnowledgeNode.inputType.RULE) && aboveTags.contains(kn.rule) == false){
    							aboveTags.add(kn.rule);
    							added = true;
    							break;
    						}
    					}
    				}
    			}
    		}while(added == true);   		
    		
    		for(Tag parent : aboveTags){
    			ArrayList<Tag> belowTags = new ArrayList<>();
    			depthFirstSearch(parent, belowTags);
    			for(Tag tg : belowTags){
    				if(tg.value.equals(item)){
    					ArrayList<Tag> onePath = new ArrayList<>();
    					ArrayList<Tag> parentToChild = pathFinder(parent, t);
    					ArrayList<Tag> parentToItem = pathFinder(parent, tg);
    					onePath.addAll(parentToChild);
    					for(int i=0; i<parentToItem.size(); i++){
    						if(onePath.contains(parentToItem.get(i)) == false){
    							onePath.add(parentToItem.get(i));
    						}
    					}
    					double currentConfidence = 0;
    					if(parentToChild.size() == 1){
    						currentConfidence = totalConfidence(parentToItem) / (parentToItem.size()-1);
    					}
    					else if(parentToItem.size() == 1){
    						currentConfidence = totalConfidence(parentToChild) / (parentToChild.size()-1);
    					}
    					else if(parentToItem.contains(t)){
    						currentConfidence = totalConfidence(parentToItem) / (parentToItem.size()-1);
    					}
    					else{
    						int length = 0;
    						ArrayList<Tag> PtoIsubList = new ArrayList<>();
    						if(parentToChild.size() > parentToItem.size()){
    							length = parentToItem.size();
    						}
    						else{
    							length = parentToChild.size();
    						}
    						
    						for(int i=1; i<length; i++){
    							if(parentToChild.get(i).equals(parentToItem.get(i)) == false){
    								for(int j=i-1; j<parentToItem.size(); j++){
    									PtoIsubList.add(parentToItem.get(j));
    								}
    								break;
    							}
    						}
    						
    						double confidence1 = totalConfidence(parentToChild);
    						double confidence2 = totalConfidence(PtoIsubList);
    						currentConfidence = (confidence1 + confidence2) / (onePath.size()-1);
    					}
    					//System.out.print(onePath.toString() + " "); //erase later
    					//System.out.println(currentConfidence); //erase later
    					if(currentConfidence > bestConfidence){
    						bestPath = onePath;
    						bestConfidence = currentConfidence;
    					}
    				}
    			}
    			
    		}
    	}
    	
    	for(int i=0; i<bestPath.size(); i++){
    		this.activeTags.add(bestPath.get(i));
    	}
    	
    }
    
    
    
    /**
     * Calculate the total confidence of a given path
     * 
     * @param path: the path needed to calculate the total confidence
     * @return the total confidence of that path
     */
    public double totalConfidence(ArrayList<Tag> path){
    	double totalConfidence = 0;
    	if(path.size() <= 1){
    		return totalConfidence;
    	}
    	
    	for(int i=0; i<path.size()-1; i++){
    		totalConfidence = totalConfidence + this.mapKN.get(path.get(i)).outputs.get(path.get(i+1));
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
    				for(int i=0; i<template.size(); i++){
    					currentPath.add(template.get(i));
    				}
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
    public void backwardSearch(double score, int confidenceLevel, int ply){  	
    	for(int i=0; i<ply; i++){
    		for(KnowledgeNode kn : this.mapKN.values()){
    			int matching = 0;
    			for(Tag t : kn.outputs.keySet()){
    				if(activeTags.contains(t) && kn.outputs.get(t) >= confidenceLevel){
    					matching++;
    				}
    			}
    			if( (double)matching/kn.outputs.size() >= score ){
    				if(kn.type.equals(KnowledgeNode.inputType.FACT) && this.activeTags.contains(kn.fact) == false){
    					this.activeTags.add(kn.fact);
    				}
    				else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION) && this.activeTags.contains(kn.recommendation) == false){
    					this.activeTags.add(kn.recommendation);
    				}
    				else if(kn.type.equals(KnowledgeNode.inputType.RULE) && this.activeTags.contains(kn.rule) == false){
    					this.activeTags.add(kn.recommendation);
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
    public void backwardSearch(double score, int confidenceLevel){
    	HashSet<Tag> pendingFacts = new HashSet<>();   	
    	do{
    		pendingFacts.clear();
    		for(KnowledgeNode kn : this.mapKN.values()){
    			int matching = 0;
    			for(Tag t : kn.outputs.keySet()){
    				if(this.activeTags.contains(t) && kn.outputs.get(t) >= confidenceLevel){
    					matching++;
    				}
    			}
    			if( (double)matching/kn.outputs.size() >= score ){
    				if(kn.type.equals(KnowledgeNode.inputType.FACT) && this.activeTags.contains(kn.fact) == false){
    					pendingFacts.add(kn.fact);
    				}
    				else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION) && this.activeTags.contains(kn.recommendation) == false){
    					pendingFacts.add(kn.recommendation);
    				}
    				else if(kn.type.equals(KnowledgeNode.inputType.RULE) && this.activeTags.contains(kn.rule) == false){
    					pendingFacts.add(kn.recommendation);
    				}
    			}
    		}
    		this.activeTags.addAll(pendingFacts);
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
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					excite(kn, tp.value);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					excite(kn, tp.value);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.activeTags.add(rc);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.activeTags.add(r);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.activeTags.add(f);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.activeTags.add(f);
    			}
    		}
    	}
    	
    	if(ply > 0 && this.activeTags.isEmpty() == false){
    		for(int i=0; i<ply; i++){
    			Tag[] activeList = this.activeTags.toArray(new Tag[0]);
    			for(int j=0; j<activeList.length; j++){
    				if(this.mapKN.containsKey(activeList[j])){
    					if(this.mapKN.get(activeList[j]).isActivated != true && this.mapKN.get(activeList[j]).activation >= this.mapKN.get(activeList[j]).threshold){
    						excite(this.mapKN.get(activeList[j]), 0);
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
     * If a string match a kn in the knowledge node network, that kn will be excite 
     */  
    public void forwardSearch(ArrayList<Tuple> NNoutputs){
    	for(Tuple tp : NNoutputs){
    		boolean found = false;
    		for(KnowledgeNode kn : this.mapKN.values()){
    			if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    				if(kn.fact.getPredicateName().equals(tp.s)){
    					excite(kn, tp.value);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    				if(kn.recommendation.equals(tp.s)){
    					excite(kn, tp.value);
    					found = true;
    				}
    			}
    			else if(kn.type.equals(KnowledgeNode.inputType.RULE)){
    				if(kn.rule.equals(tp.s)){
    					excite(kn, tp.value);
    					found = true;
    				}
    			}
    		}
    		if(found == false){
    			if(tp.s.charAt(0) == '@'){
    				Recommendation rc = new Recommendation(tp.s);
    				this.activeTags.add(rc);
    			}
    			else if(tp.s.contains("->")){
    				Rule r = new Rule(tp.s);
    				this.activeTags.add(r);
    			}
    			else if(tp.s.matches(".*\\(.*\\).*")){
    				Fact f = new Fact(tp.s);
    				this.activeTags.add(f);
    			}
    			else{
    				String str = tp.s + "()";
    				Fact f = new Fact(str);
    				this.activeTags.add(f);
    			}
    		}
    	}
    	
    	boolean allActived;
   		do{
   			allActived = true;
   			Tag[] activeList = this.activeTags.toArray(new Tag[0]);
   			for(int i=0; i<activeList.length; i++){
   				if(this.mapKN.containsKey(activeList[i])){
   					if(this.mapKN.get(activeList[i]).isActivated != true && this.mapKN.get(activeList[i]).activation >= this.mapKN.get(activeList[i]).threshold){
   						excite(this.mapKN.get(activeList[i]), 0);
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
    		if(kn.type.equals(KnowledgeNode.inputType.FACT)){
    			this.activeTags.add(kn.fact);
    		}
    		else if(kn.type.equals(KnowledgeNode.inputType.RECOMMENDATION)){
    			this.activeTags.add(kn.recommendation);
    		}
    		else{
    			this.activeTags.add(kn.rule);
    		}
    		kn.isActivated = true;
    		this.activeTags.addAll(fire(kn));
    	}
    }
    
    
    /**
     * Fires a Knowledge Node.
     * 
     * @param kn: Knowledge Node to fire
     * @return a Set of Tags found from the output list of kn that have activation >= threshold
     */
    public HashSet<Tag> fire(KnowledgeNode kn) {
        HashSet<Tag> pendingTags = new HashSet<>();
        for (Tag t : kn.outputs.keySet()) {
        	KnowledgeNode currentKN = mapKN.get(t);
        	currentKN.activation+=100;
        	if(currentKN.activation >= currentKN.threshold){
        		pendingTags.add(t);
        	}
        }
        return pendingTags;
    }
    
    /**
     * Build a relation tree using Tags from the activeTags list
     * This method help the programmer to understand what is going on from the result of each searching
     */
    public void buildTree(){
    	ArrayList<Tag> toRemove = new ArrayList<>();
    	for(Tag c : this.activeTags){
    		for(Tag p : this.activeTags){
    			if(this.mapKN.containsKey(c) && this.mapKN.containsKey(p)){
    				if(this.mapKN.get(p).outputs.containsKey(c)){
    					Link lk = new Link(p, this.mapKN.get(p).outputs.get(c), c);
    					this.links.add(lk);
    				}
    			}
    		}
    	}
    	this.treeRoots.addAll(this.activeTags);
    	for(Tag c : this.treeRoots){
    		for(Tag p : this.treeRoots){
    			if(this.mapKN.containsKey(c) && this.mapKN.containsKey(p)){
    				if(this.mapKN.get(p).outputs.containsKey(c)){
    					toRemove.add(c);
    				}
    			}
    		}
    	}
    	for(int i=0; i<toRemove.size(); i++){
    		this.treeRoots.remove(toRemove.get(i));
    	}
    }
    
}
