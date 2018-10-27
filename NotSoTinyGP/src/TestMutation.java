import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import mockimpl.MockFactory;
import mockimpl.NumNode;
import mockimpl.OpNode;
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
		
		NodeFactory factory = new MockFactory(0.5);
		PopulationGenerator generator = new GrowGenerator(factory, 3);
		
		Mutation stm = new SubtreeMutation(generator, new SubtreeCrossover());
		Mutation ptm = new PointMutation(factory, 0.5);

		Node n1 = new OpNode(new OpNode(new NumNode(4), new NumNode(5)), new NumNode(3));
		Node n2 = new OpNode(new NumNode(7), new OpNode(new NumNode(9), new NumNode(0)));
		
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		System.out.println("\nSubtree mutation");
		System.out.println("n1 = " + stm.mutate(n1));
		System.out.println("n2 = " + stm.mutate(n2));
		
		System.out.println("\nPoint mutation");
		System.out.println("n1 = " + ptm.mutate(n1));
		System.out.println("n2 = " + ptm.mutate(n2));
	}

}
