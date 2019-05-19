package operators;

import java.util.Random;
import java.util.function.Predicate;

import model.FunctionNode;
import model.Node;
import model.TerminalNode;
import visitor.CountVisitor;
import visitor.RandAccessVisitor;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/24RecombinationandMutation.html
 * 
 *  The uniform selection of crossover points leads to frequently exchanging only very small subtrees.
 *  To counter this, Koza suggested the widely used approach of choosing functions 90% of the time and leaves 10% of the time.
 */

public class SubtreeCrossover implements Crossover {

	private static final double DEF_PFUNCT = 0.9; // see class comment above
	
	private Random random;
	// probability of choosing function nodes as crossover points
	private double pFunction;
	
	public SubtreeCrossover(Random random, double pFunction) {
		this.random = random;
		this.pFunction = pFunction;
	}

	public SubtreeCrossover(Random random) {
		this(random, DEF_PFUNCT);
	}

	@Override
	public Node apply(Node n1, Node n2) {
		Node offspring = n1.clone();
		Node xoverPnt1 = selectXOverPoint(offspring);
		Node xoverPnt2 = selectXOverPoint(n2).clone();
		
		// DEBUG
//		System.out.println("xoPnt1 = " + xoverPnt1);
//		System.out.println("xoPnt2 = " + xoverPnt2);
		
		if(xoverPnt1.isRoot()) {
			xoverPnt2.makeRoot(); // xoverPnt2.parent should have already been set to null by clone()
			return xoverPnt2;
		}
		
		// if xoverPnt1 is not the root, it has a parent
		Node parent = xoverPnt1.getParent();
		Node[] children = parent.getChildren();
		children[indexOf(xoverPnt1, children)] = xoverPnt2;
		parent.setChildren(children); // necessary! see FunctionNode.setChildren()
		
		return offspring;
	}

	private Node selectXOverPoint(Node tree) {
		boolean selectFctNode = random.nextDouble() < pFunction;
		Predicate<Node> predicate = n -> selectFctNode ? n instanceof FunctionNode : n instanceof TerminalNode;
		
		CountVisitor countVisitor = new CountVisitor(predicate);
		tree.accept(countVisitor);
		
		if(countVisitor.getSize() == 0)
			return tree; // no nodes of the selected type, use the root
		else {
			int index = random.nextInt(countVisitor.getSize());
			RandAccessVisitor rav = new RandAccessVisitor(index, predicate);
			tree.accept(rav);
			return rav.getNode().get();
		}
	}

	private int indexOf(Node n, Node[] array) {
		int i;
		// != tests pointers equality
		for(i=0; i<array.length && array[i]!=n; i++);
		return i;
	}

}
