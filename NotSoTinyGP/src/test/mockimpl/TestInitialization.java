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
		
		// p = probability of choosing a terminal rather than a function (getRandomNode())
		NodeFactory factory = new MockFactory(random, 0.5);
		
		PopulationGenerator full = new FullGenerator(factory, 3);
		PopulationGenerator grow = new GrowGenerator(factory, 3);
		PopulationGenerator ramped = new RampedHalfAndHalfGenerator(random, factory, 3, 7);
		
		System.out.println("FullGenerator:");
		generateAndCount(full);
		System.out.println("----------------");
		
		System.out.println("GrowGenerator:");
		generateAndCount(grow);
		System.out.println("----------------");
		
		System.out.println("RampedGenerator:");
		generateAndCount(ramped);
		System.out.println("----------------");
		
		// generate and print a random individual
		Node[] pop = ramped.generate(1);
		System.out.println("Random individual: " + pop[0]);
		CountVisitor v = new CountVisitor();
		pop[0].accept(v);
		System.out.println("Depth = " + v.getDepth() + ", size = " + v.getSize());
	}
	
	private static void generateAndCount(PopulationGenerator generator) {
		Node[] pop = generator.generate(POPSIZE);
		PopulationAnalyser.printStats(pop);
	}

}
