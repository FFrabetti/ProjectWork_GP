package test.mockimpl;
import java.util.Random;

import examples.mockimpl.MockFactory;
import initialization.FullGenerator;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import initialization.RampedHalfAndHalfGenerator;
import model.Node;
import model.NodeFactory;
import utils.PopulationAnalyser;
import visitor.CountVisitor;

public class TestInitialization {

	public static final int POPSIZE = 500;
	
	public static void main(String[] args) {
		Random random = new Random();
		
		// probability of choosing a terminal rather than a function (getRandomNode())
		double pTerm = 0.5;
		NodeFactory factory = new MockFactory(random, pTerm);
		
		int fullDepth = 3;
		PopulationGenerator full = new FullGenerator(factory, fullDepth);
		
		int growDepth = 3;
		PopulationGenerator grow = new GrowGenerator(factory, growDepth);
		
		int minDepth = 3;
		int maxDepth = 7;
		PopulationGenerator ramped = new RampedHalfAndHalfGenerator(random, factory, minDepth, maxDepth);
		
		System.out.println("FullGenerator (depth = " + fullDepth + "):");
		generateAndCount(full, POPSIZE);
		System.out.println("----------------");
		
		System.out.println("GrowGenerator (depth = " + growDepth + "):");
		generateAndCount(grow, POPSIZE);
		System.out.println("----------------");
		
		System.out.println("RampedGenerator (depth = " + minDepth + "-" + maxDepth + "):");
		generateAndCount(ramped, POPSIZE);
		System.out.println("----------------");
		
		// generate and print a random individual
		Node n = ramped.generate(1)[0];
		System.out.println("Random individual: " + n);
		CountVisitor v = new CountVisitor();
		n.accept(v);
		System.out.println("depth = " + v.getDepth() + ", size = " + v.getSize());
	}
	
	private static void generateAndCount(PopulationGenerator generator, int size) {
		PopulationAnalyser.printStats(generator.generate(size));
	}

}
