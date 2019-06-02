package test.mockimpl;
import java.util.Random;

import examples.mockimpl.NumNode;
import examples.mockimpl.OpNode;
import model.Node;
import operators.SubtreeCrossover;

public class TestCrossover {

	public static void main(String[] args) {
		Random random = new Random(1);
		
		// n1 = ((4,6),2)
		Node n1 = new OpNode(new OpNode(new NumNode(4), new NumNode(6)), new NumNode(2));
		// n2 = (7,(9,3))
		Node n2 = new OpNode(new NumNode(7), new OpNode(new NumNode(9), new NumNode(3)));
		
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		applyCrossover(random, 0, n1, n2);
		applyCrossover(random, 0.5, n1, n2);
		applyCrossover(random, 1, n1, n2);
	}

	private static void applyCrossover(Random random, double pFunction, Node n1, Node n2) {
		System.out.println("offspring (pFunction=" + pFunction + ") = " + 
				new SubtreeCrossover(random, pFunction).apply(n1, n2));
	}

}
