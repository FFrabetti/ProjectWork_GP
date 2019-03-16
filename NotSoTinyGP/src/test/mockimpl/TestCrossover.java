package test.mockimpl;
import examples.mockimpl.NumNode;
import examples.mockimpl.OpNode;
import model.Node;
import operators.Crossover;
import operators.SubtreeCrossover;
import utils.RandomGenerator;

public class TestCrossover {

	public static void main(String[] args) {
		RandomGenerator.getInstance().setSeed(1);
		
		// n1 = ((4,6),2)
		Node n1 = new OpNode(new OpNode(new NumNode(4), new NumNode(6)), new NumNode(2));
		// n2 = (7,(9,3))
		Node n2 = new OpNode(new NumNode(7), new OpNode(new NumNode(9), new NumNode(3)));
		
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		Crossover xo = new SubtreeCrossover();		// 90% function nodes
		Crossover xof = new SubtreeCrossover(1);	// function nodes as crossover points
		Crossover xot = new SubtreeCrossover(0);	// terminal nodes as crossover points
		
		System.out.println("offspring1 (90% func) = " + xo.apply(n1, n2));
		System.out.println("offspring2 (func) = " + xof.apply(n1, n2));
		System.out.println("offspring3 (term) = " + xot.apply(n1, n2));
	}

}
