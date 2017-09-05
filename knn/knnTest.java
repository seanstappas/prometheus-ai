package knn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class knnTest {
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
			System.out.println(animal.get(i).toString());
			System.out.println(" ");
		}
		System.out.println(" ");

		//testing fire method
//		HashSet<Tag> resultOne = new HashSet<>();		
//		for(KnowledgeNode kn : animal){
//			if(kn.type == KnowledgeNode.inputType.FACT){
//				if(kn.fact.getPredicateName().equals("husky"))
//				{
//					resultOne.addAll(relation.fire(kn));
//				}
//			}
//		}
//		System.out.println(resultOne.toString());
		
		
		//testing excite method
		/*
		for(KnowledgeNode kn : animal){
			if(kn.fact != null && kn.fact.getPredicateName().equals("husky")){
				relation.excite(kn, 10);
			}
		}
		System.out.println(relation.getActiveTags().toString());
		*/
		
		//testing forward searching with or without ply input
		/*
		ArrayList<Tuple> inputs = new ArrayList<>();
		Tuple data1 = new Tuple("husky", 10); inputs.add(data1);
		Tuple data2 = new Tuple("rogdoll", 10); inputs.add(data2);
		Tuple data4 = new Tuple("missing", 10); inputs.add(data4);
		relation.forwardSearch(inputs, 1);
		System.out.println(relation.getActiveTags().toString());	
		*/
		
		//testing backward searching without ply input
		/*
		relation.addFiredTag(new Fact("pet(dog>100,cat>80)"));
		relation.addFiredTag(new Fact("animal(multicellular,vertebrate,invertebrate)"));
		relation.backwardSearch(1, 20);
		System.out.println(relation.getActiveTags().toString());
		*/
		
		//testing backward searching with ply input
		/*
		relation.addFiredTag(new Fact("pet(dog>100,cat>80)"));
		relation.addFiredTag(new Fact("animal(multicellular,vertebrate,invertebrate)"));
		relation.backwardSearch(1, 55, 1);
		System.out.println(relation.getActiveTags().toString());
		*/
		
		//testing lambda searching
		
//		ArrayList<Tag> result = new ArrayList<>();
//		//relation.depthFirstSearch(new Fact("husky(Ranger,male,length>58,weight=26)"), result);
//		ArrayList<Tag> p = relation.pathFinder(new Fact("husky(Ranger,male,length>58,weight=26)"), new Fact("pet(dog>100,cat>80)"));
//		double t = relation.totalConfidence(p);
//		System.out.println(t);
		
		
//		relation.addFiredTag(new Fact("animal(multicellular,vertebrate,invertebrate)"));
//		relation.lambdaSearch("pet(dog>100,cat>80)");
//		System.out.println(relation.getActiveTags().toString());
		
	}
}
