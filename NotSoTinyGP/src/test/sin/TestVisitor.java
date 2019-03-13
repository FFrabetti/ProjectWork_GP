package test.sin;

import java.util.HashMap;
import java.util.Map;

import examples.sin.EvalVisitor;
import examples.sin.NumNode;
import examples.sin.OpNode;
import examples.sin.VarNode;
import model.Node;
import visitor.CountVisitor;

public class TestVisitor {

	public static void main(String[] args) {
		// 3*4 + 5/0 = 12 + 1 = 13
		Node n1 = new OpNode("+",
				new OpNode("*", new NumNode(3), new NumNode(4)),
				new OpNode("/", new NumNode(5), new NumNode(0))
		);
		
		// 2 - (x-1) = 3 - x = -3 (x=6)
		Node n2 = new OpNode("-", new NumNode(2), new OpNode("-", new VarNode("x"), new NumNode(1)));
		
		Map<String,Double> env = new HashMap<>();
		env.put("x", 6.0);
		
		printVisitResults(n1, env);
		System.out.println("----------------");
		printVisitResults(n2, env);
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