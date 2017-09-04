package knn;

import tags.Fact;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class KNN_Testing {
	public static void main(String[] args){	
		ArrayList<KnowledgeNode> animal = new ArrayList<>();
		KnowledgeNodeNetwork relation = new KnowledgeNodeNetwork();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader("./animalData")); //change the directory for the test file to run
			String line;
			while( (line = br.readLine()) != null){
				String[] info = line.split(";\\s+");
				KnowledgeNode kn = new KnowledgeNode(info);
				animal.add(kn);
			}
			br.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		for(int i=0; i<animal.size(); i++){
			relation.addKN(animal.get(i));
			//System.out.println(animal.get(i).toString());
			//System.out.println(" ");
		}
		//System.out.println(" ");

		//testing fire method	
//		for(KnowledgeNode kn : animal){
//			if(kn.type == KnowledgeNode.inputType.FACT){
//				if(kn.fact.getPredicateName().equals("dog"))
//				{
//					kn.objectTruth = 100;
//					relation.fire(kn);
//				}
//				else if(kn.fact.getPredicateName().equals("cat")){
//					kn.objectTruth = 100;
//					relation.fire(kn);
//				}
//			}
//		}
//		System.out.println(relation.getActiveTags().toString());
//		
//		for(KnowledgeNode kn : animal){
//			if(kn.type == KnowledgeNode.inputType.FACT){
//				if(kn.fact.getPredicateName().equals("dog"))
//				{
//					kn.objectTruth = 85;
//					relation.updateConfidence(kn);
//				}
//			}
//		}
//		System.out.println(relation.getActiveTags().toString());
		
		
		//testing excite method		
//		for(KnowledgeNode kn : animal){
//			if(kn.fact != null && kn.fact.getPredicateName().equals("dog")){
//				relation.excite(kn, 10);
//			}
//		}
//		System.out.println(relation.getActiveTags().toString());
//		
//		for(KnowledgeNode kn : animal){
//			if(kn.fact != null && kn.fact.getPredicateName().equals("husky")){
//				relation.excite(kn, 10);
//			}
//			else if(kn.fact != null && kn.fact.getPredicateName().equals("cat")){
//				relation.excite(kn, 10);
//			}
//		}
//		System.out.println(relation.getActiveTags().toString());
		
		
		//testing forward searching with and without ply input		
//		ArrayList<Tuple> inputs = new ArrayList<>();
//		Tuple data1 = new Tuple("dog", 10); inputs.add(data1);
//		Tuple data2 = new Tuple("cat", 10); inputs.add(data2);		
//		
//		relation.forwardSearch(inputs, 1);
//		System.out.println("Input for forward search: " + relation.getInputTags().toString());
//		System.out.println(relation.getActiveTags().toString());	
		
		
		//testing backward searching with and without ply input		
//		ArrayList<Tuple> inputs = new ArrayList<>();
//		Tuple data1 = new Tuple("calm", 10); inputs.add(data1);
//		Tuple data2 = new Tuple("coward", 10); inputs.add(data2);
//		relation.backwardSearch(inputs, 0.5, 2);
//		System.out.println("Input for backward search: " + relation.getInputTags().toString());
//		System.out.println(relation.getActiveTags().toString());		
		
		//testing lambda searching				
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("mammal", 10); inputs.add(data1);
		relation.lambdaSearch(inputs, new Fact("fish(vertebrate,water)"));
		System.out.println("Input for lambda search: " + relation.getInputTags().toString());
		System.out.println(relation.getActiveTags().toString());
		
		//path
//		ArrayList<Tag> bads = new ArrayList<>();
//		bads.add(new Fact("fur(strands,insulator)"));
//		ArrayList<Tag> path = new ArrayList<>();
//		path = relation.pathFinder(new Fact("cat(feline,length>50,weight>20)"), new Fact("mammal(vertebrate,land)"), bads);
//		System.out.println(path.toString());
	}
}
