package testmulino;

import examples.regression.VarNode;
import model.FunctionNode;
import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class MulinoVarVisitor implements NodeVisitor {

	private String varName;

	private int minDepth;
	private int totDepth;
	private int count;

	private int currDepth; // keeps track of the "visiting depth"

	public MulinoVarVisitor(String varName) {
		this.varName = varName;

		currDepth = 0;

		minDepth = Integer.MAX_VALUE;
		totDepth = 0;
		count = 0;
	}

	@Override
	public void visit(FunctionNode node) {
		currDepth++;
		for (Node child : node.getChildren())
			child.accept(this);
		currDepth--;
	}

	@Override
	public void visit(TerminalNode node) {
		if (node instanceof VarNode && ((VarNode) node).getName().equals(varName)) {
			count++;
			totDepth += currDepth;

			if (currDepth < minDepth)
				minDepth = currDepth;
		}
	}

	public int getCount() {
		return count;
	}
	
	public int getMinDepth() {
		return minDepth;
	}
	
	public double getAvgDepth() {
		return count !=0 ? totDepth/(float)count : 0;
	}
	
}
