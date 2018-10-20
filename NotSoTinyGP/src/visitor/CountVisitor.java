package visitor;

import model.Node;

public class CountVisitor implements NodeVisitor {

	private int depth;
	private int size;
	
	public CountVisitor() {
		depth = 0;
		size = 0;
	}
	
	public int getDepth() {
		return depth;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void visit(Node node) {
		recursiveVisit(node, 0);
	}

	private void recursiveVisit(Node node, int currDepth) {
		size++;
		
		Node[] children = node.getChildren();
		if(children.length == 0) { // leaf node
			if(currDepth > depth)
				depth = currDepth;
		}
		else {
			for(Node child : children)
				recursiveVisit(child, currDepth+1);
		}
	}
	
}
