package visitor;

import java.util.function.Predicate;

import model.FunctionNode;
import model.Node;
import model.TerminalNode;

public class CountVisitor implements NodeVisitor {

	private int depth;		// max depth of the tree
	private int currDepth;	// keeps track of the "visiting depth"
	private int size;		// total number of nodes
	
	// to filter the counting of size, e.g. based on the type of nodes
	private Predicate<Node> predicate;
	
	public CountVisitor(Predicate<Node> predicate) {
		this.predicate = predicate;
		depth = 0;
		currDepth = 0;
		size = 0;
	}
	
	public CountVisitor() {
		this(null); // the default is count all (see count())
	}
	
	public int getDepth() {
		return depth;
	}

	public int getSize() {
		return size;
	}

	private void count(Node node) {
		if(predicate == null || predicate.test(node))
			size++;
	}
	
	@Override
	public void visit(FunctionNode node) {
		count(node);
		
		currDepth++;
		for(Node child : node.getChildren())
			child.accept(this);
		currDepth--;
	}

	@Override
	public void visit(TerminalNode node) {
		count(node);
		
		if(currDepth > depth) // new maximum found
			depth = currDepth;
	}
	
}
