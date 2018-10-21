import initialization.FullGenerator;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import initialization.RampedHalfAndHalfGenerator;
import mockimpl.MockFactory;
import model.Node;
import model.NodeFactory;
import visitor.CountVisitor;

public class TestMain {

	public static void main(String[] args) {

		NodeFactory factory = new MockFactory(0.5);
		
		PopulationGenerator full = new FullGenerator(factory, 5);
		PopulationGenerator grow = new GrowGenerator(factory, 5);
		PopulationGenerator ramped = new RampedHalfAndHalfGenerator(factory, 3, 7);
		
		System.out.println("FullGenerator:");
		generateAndCount(full);
		System.out.println("----------------");
		
		System.out.println("GrowGenerator:");
		generateAndCount(grow);
		System.out.println("----------------");
		
		System.out.println("RampedGenerator:");
		generateAndCount(ramped);
	}
	
	private static void generateAndCount(PopulationGenerator generator) {
		Node[] pop = generator.generate(500);
		int totDepth = 0;
		int totSize = 0;
		
		int maxDepth = 0;
		int minDepth = Integer.MAX_VALUE;
		int maxSize = 0;
		int minSize = Integer.MAX_VALUE;
		
		for(Node n : pop) {
			CountVisitor v = new CountVisitor();
			n.accept(v);
			int depth = v.getDepth();
			int size = v.getSize();
			
			totDepth += depth;
			totSize += size;
			
			if(depth > maxDepth)
				maxDepth = depth;
			if(depth < minDepth)
				minDepth = depth;
			
			if(size > maxSize)
				maxSize = size;
			if(size < minSize)
				minSize = size;
		}
		
		System.out.println("Av. depth = " + totDepth/(float)pop.length);
		System.out.println("Min depth = " + minDepth + ", Max depth = " + maxDepth);		
		System.out.println("Av. size = " + totSize/(float)pop.length);
		System.out.println("Min size = " + minSize + ", Max size = " + maxSize);
	}

}
