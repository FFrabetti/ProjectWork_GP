package test.sin;

import java.util.HashMap;
import java.util.Map;

import examples.sin.*;
import model.Node;
import visitor.CountVisitor;

public class TestVisitors {

	public static void main(String[] args) {
		// test protected division
		// 3*4 + 4/(-0.0001) = 12 - 4 = 8
		Node n1 = new PlusNode(
				new TimesNode(new DoubleNode(3), new DoubleNode(4)),
				new DivNode(new DoubleNode(4), new DoubleNode(-0.0001))
		);

		// test variables
		// 2 - (x-1) = 3 - x = -3 (with x=6)
		Node n2 = new MinusNode(new DoubleNode(2), new MinusNode(new VarNode("x"), new DoubleNode(1)));
		Map<String,Double> env = new HashMap<>();
		env.put("x", 6.0);

		// test operators priorities
		// 2 + 3*(4+5) = 2 + 27 = 29
		Node n3 = new PlusNode(
			new DoubleNode(2),
			new TimesNode(
				new DoubleNode(3),
				new PlusNode(new DoubleNode(4), new DoubleNode(5))
		));

		Node[] nodes = new Node[] {n1, n2, n3};
		for(Node n : nodes) {
			printVisitResults(n, env);
			System.out.println("----------------");
		}
	}

	private static void printVisitResults(Node n, Map<String,Double> env) {
		System.out.println(n);
		System.out.println("Environment: " + env);
		
		CountVisitor cv = new CountVisitor();
		n.accept(cv);
		System.out.println("size = " + cv.getSize() + ", depth = " + cv.getDepth());
		
		EvalVisitor ev = new EvalVisitor(env);
		n.accept(ev);
		System.out.println("value = " + ev.getResult());
	}
	
}
