package visitor;

import examples.regression.OpNode;
import model.FunctionNode;
import model.Node;
import model.TerminalNode;

public class PrintDepthVisitor implements NodeVisitor {

	private int maxDepth;
	private int currDepth;
	private StringBuilder sb;
	
	public PrintDepthVisitor(int maxDepth) {
		this.maxDepth = maxDepth;
		currDepth = 0;
		sb = new StringBuilder();
	}
	
	private void append(String str) {
		sb.append(str);
		sb.append(' ');
	}
	
	@Override
	public void visit(FunctionNode node) {
		if(currDepth >= maxDepth) {
			append("...");
			return;
		}
		
		if(node instanceof OpNode)
			append(((OpNode)node).getOperator());
		
		currDepth++;
		for(Node child : node.getChildren())
			child.accept(this);
		currDepth--;
	}

	@Override
	public void visit(TerminalNode node) {
		append(node.toString());
	}

	@Override
	public String toString() {
		return sb.toString();
	}
	
}
