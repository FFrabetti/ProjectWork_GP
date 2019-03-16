package test.mockimpl;
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
import utils.RandomGenerator;

public class TestMutation {

	public static void main(String[] args) {
		RandomGenerator.getInstance().setSeed(2);
		
		NodeFactory factory = new MockFactory(0.5); // getRandomNode() selects terminals 50% of the times
		PopulationGenerator generator = new GrowGenerator(factory, 3); // maxDepth = 3
		
		Mutation stm = new SubtreeMutation(generator, new SubtreeCrossover());
		Mutation ptm = new PointMutation(factory, 0.5); // pNode = 0.5 (50% of nodes will be mutated)

		// n1 = ((4,6),2)
		Node n1 = new OpNode(new OpNode(new NumNode(4), new NumNode(6)), new NumNode(2));
		// n2 = (7,(9,3))
		Node n2 = new OpNode(new NumNode(7), new OpNode(new NumNode(9), new NumNode(3)));
		
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		System.out.println("\nSubtree mutation");
		System.out.println("crossover with new ind. created with Grow (maxDepth=3, 50% chances of terminal)");
		System.out.println("90% prob. of selecting a function as crossover point");
		System.out.println("n1.1 = " + stm.mutate(n1));
		System.out.println("n2.1 = " + stm.mutate(n2));
		
		// mutating an OpNode, in this case, has no effect
		System.out.println("\nPoint mutation (pNode=0.5)");
		System.out.println("n1.2 = " + ptm.mutate(n1));
		System.out.println("n2.2 = " + ptm.mutate(n2));
		
		// n3 = ((11,12),10)
		// note that MockFactory cannot produce such NumNodes, with getValue()>9
		// this is useful to check whether a node is mutated or not
		Node n3 = new OpNode(new OpNode(new NumNode(11), new NumNode(12)), new NumNode(10));
		System.out.println("\nPoint mutation of n3 = " + n3);
		System.out.println("all points mutated: n3.1 = " + new PointMutation(factory, 1).mutate(n3));
		System.out.println("none mutated: n3.2 = " + new PointMutation(factory, 0).mutate(n3));		
	}

}
