package operators;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import model.FunctionNode;
import model.Node;
import model.TerminalNode;
import utils.RandomGenerator;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/24RecombinationandMutation.html
 * 
 *  The uniform selection of crossover points leads to frequently exchanging only very small subtrees.
 *  To counter this, Koza suggested the widely used approach of choosing functions 90% of the time and leaves 10% of the time.
 */

public class SubtreeCrossover implements Crossover {

	// probability of choosing function nodes as crossover points
	private double pFunction;
	
	public SubtreeCrossover(double pFunction) {
		this.pFunction = pFunction;
	}

	public SubtreeCrossover() {
		this(0.9);
	}

	@Override
	public Node apply(Node n1, Node n2) {
		Node offspring = n1.clone();
		Node xoverPnt1 = selectXOverPoint(offspring);
		Node xoverPnt2 = selectXOverPoint(n2).clone();
		
		if(xoverPnt1.isRoot()) {
			xoverPnt2.makeRoot(); // (xoverPnt2.parent should already be set to null by clone())
			return xoverPnt2;
		}
		
		Node parent = xoverPnt1.getParent();
		Node[] children = parent.getChildren();
		children[indexOf(xoverPnt1)] = xoverPnt2;
		parent.setChildren(children); // necessary! see FunctionNode.setChildren()
		
		return offspring;
	}

	private Node selectXOverPoint(Node tree) {
		boolean swapFunction = RandomGenerator.getInstance().nextDouble() < pFunction;
		Predicate<Node> predicate = n -> swapFunction ? n instanceof FunctionNode : n instanceof TerminalNode;
		
		List<Node> list = new LinkedList<>();
		linearizeNodes(tree, list, predicate);
		
		if(list.size() == 0)
			return tree;
		else
			return list.get(RandomGenerator.getInstance().nextInt(list.size()));
	}

	private void linearizeNodes(Node n, List<Node> list, Predicate<Node> predicate) {
		if(predicate.test(n))
			list.add(n);
		
		for(Node child : n.getChildren())
			linearizeNodes(child, list, predicate);
	}

	private int indexOf(Node n) {
		Node[] children = n.getParent().getChildren();
		int i;
		// != tests pointers equality
		for(i=0; i<children.length && children[i]!=n; i++);
		return i;
	}

}
