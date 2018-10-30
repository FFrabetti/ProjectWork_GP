package visitor;

import model.FunctionNode;
import model.Node;
import model.TerminalNode;

public class CountVisitor implements NodeVisitor {

	private int depth;
	private int currDepth;
	private int size;
	
	public CountVisitor() {
		depth = 0;
		currDepth = 0;
		size = 0;
	}
	
	public int getDepth() {
		return depth;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void visit(FunctionNode node) {
		size++;		
		currDepth++;

		int localDepth = currDepth;
		for(Node child : node.getChildren()) {
			child.accept(this);
			currDepth = localDepth;
		}
	}

	@Override
	public void visit(TerminalNode node) {
		size++;
		
		if(currDepth > depth)
			depth = currDepth;
	}
	
}
