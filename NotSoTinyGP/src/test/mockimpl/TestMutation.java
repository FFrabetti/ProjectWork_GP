package test.mockimpl;
import java.util.Random;

import examples.mockimpl.MockFactory;
import examples.mockimpl.NumNode;
import examples.mockimpl.OpNode;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import model.Node;
import model.NodeFactory;
import operators.Mutation;
import operators.PointMutation;
import operators.SubtreeCrossover;
import operators.SubtreeMutation;

public class TestMutation {

	public static void main(String[] args) {
		Random random = new Random(1);
		
		double pTerm = 0.5; // getRandomNode() selects terminals pTerm (%) of the times
		NodeFactory factory = new MockFactory(random, pTerm);
		
		int maxDepth = 3;
		PopulationGenerator generator = new GrowGenerator(factory, maxDepth);
		double pFunction = 0.9; // probability of selecting a function as crossover point
		Mutation stm = new SubtreeMutation(generator, new SubtreeCrossover(random, pFunction));
		
		double pNode = 0.5; // percentage of nodes that will be mutated
		Mutation ptm = new PointMutation(random, factory, pNode);
		// NOTE: mutating an OpNode, in this case, has no effect
		
		// note that MockFactory cannot produce such NumNodes, with getValue()>9
		// this is useful to check whether a node has been mutated or not
		
		// n1 = ((14,16),12)
		Node n1 = new OpNode(new OpNode(new NumNode(14), new NumNode(16)), new NumNode(12));
		// n2 = (17,(19,13))
		Node n2 = new OpNode(new NumNode(17), new OpNode(new NumNode(19), new NumNode(13)));
		
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		System.out.println("\nSubtree mutation");
		System.out.println("\t NodeFactory.pTerm = " + pTerm);
		System.out.println("\t GrowGenerator.maxDepth = " + maxDepth);
		System.out.println("\t SubtreeCrossover.pFunction = " + pFunction);
		System.out.println("mutate(n1) = " + stm.mutate(n1));
		System.out.println("mutate(n2) = " + stm.mutate(n2));
		
		System.out.println("\nPoint mutation");
		System.out.println("\t NodeFactory.pTerm = " + pTerm);
		System.out.println("\t PointMutation.pNode = " + pNode);
		System.out.println("mutate(n1) = " + ptm.mutate(n1));
		System.out.println("mutate(n2) = " + ptm.mutate(n2));
		
		// n3 = ((11,12),10)
		Node n3 = new OpNode(new OpNode(new NumNode(11), new NumNode(12)), new NumNode(10));
		System.out.println("\nPoint mutation of n3 = " + n3);
		System.out.println("pNode=1 -> all nodes mutated = " + new PointMutation(random, factory, 1).mutate(n3));
		System.out.println("pNode=0 -> none mutated = " + new PointMutation(random, factory, 0).mutate(n3));		
	}

}
