package tags;

public class TagTests {
	public static void main(String[] args){
		
		//Test how to create a fact Tag
		System.out.println("Fact tag testing ...");
		
		Fact factTag = new Fact("Dog(height = 55)");
		Fact factTag2 = new Fact("Dog(weight=55)");
		System.out.println("The value is " + factTag.toString());
		System.out.println("The predicateName is " + factTag.getPredicateName());
		System.out.println("The arguments are " + factTag.getArguments().get(0).getSymbol());
		System.out.println(factTag.toString());
		//System.out.println(factTag.matches(factTag2).doesMatch);
				
		/*
		//Test how to create a rule Tag	
		System.out.println("Rule Tag testing ... ");
		
		Fact human = new Fact("human");
		Fact cat = new Fact("cat");
		Fact frog = new Fact("frog");
		Tag geneticVariance = new Fact("geneticVariance");
		Tag haploid = new Fact("haploid");
		
		Fact father = new Fact("father");
		Fact mother = new Fact("mother");
		Fact son = new Fact("son");
		Fact daughter = new Fact("daugther");
		Fact[] parents = {father, mother};
		Fact[] children = {son, daughter};
		Tag sexualReproduction = new Rule(parents, children);	
		Fact[] condistions = {human, cat, frog};
		Tag[] consequences = {geneticVariance, sexualReproduction, haploid};
		Tag meiosis = new Rule(condistions, consequences);
		System.out.println("The value of meiosis rule is " + meiosis.value);
		System.out.println("");
		
		//Test how to create a recommendation Tag
		System.out.println("Recommendation testing ... ");
		
		Tag giveBone = new Recommendation("giveBone");
		System.out.println("Input value String = giveBone");
		System.out.println("Out value is " + giveBone.toString());
		*/
	}
}
