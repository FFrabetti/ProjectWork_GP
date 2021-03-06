package examples.regression;

import model.Node;

public class PlusNode extends OpNode {

	private static final String OP = "+";
	
	public PlusNode(Node left, Node right) {
		super(left, right);
	}
	
	PlusNode() {}
	
	@Override
	public String getOperator() {
		return OP;
	}

	@Override
	public Node clone() {
		return new PlusNode(getLeft().clone(), getRight().clone());
	}

}
