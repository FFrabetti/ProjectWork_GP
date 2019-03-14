package visitor;

import java.util.Optional;
import java.util.function.Predicate;

import model.FunctionNode;
import model.Node;
import model.TerminalNode;

public class RandAccessVisitor implements NodeVisitor {

	private int index;
	private int count;
	private Node found;
	
	// to filter the counting, e.g. based on the type of nodes
	private Predicate<Node> predicate;
	
	public RandAccessVisitor(int index, Predicate<Node> predicate) {
		this.index = index + 1; // from input index [0,n-1] to [1,n] (count)
		count = 0;
		this.predicate = predicate;
	}
	
	public RandAccessVisitor(int index) {
		this(index, null); // the default is count all (see count())
	}
	
	public Optional<Node> getNode() {
		return Optional.ofNullable(found);
	}
	
	private void count(Node node) {
		if(predicate == null || predicate.test(node))
			count++;
	}
	
	private boolean isFound(Node node) {
		if(count == index)
			found = node;
		return count == index;
	}
	
	@Override
	public void visit(FunctionNode node) {
		count(node);
		if(isFound(node))
			return;
		
		for(Node child : node.getChildren()) {
			child.accept(this);
			if(isFound(child))
				return;
		}
	}

	@Override
	public void visit(TerminalNode node) {
		count(node);
	}
	
}
