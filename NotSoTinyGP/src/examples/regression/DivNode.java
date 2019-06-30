package examples.regression;

import model.Node;

public class DivNode extends OpNode {

	private static final String OP = "/";
	
	public DivNode(Node left, Node right) {
		super(left, right);
	}
	
	DivNode() {}
	
	@Override
	public String getOperator() {
		return OP;
	}

	@Override
	public Node clone() {
		return new DivNode(getLeft().clone(), getRight().clone());
	}

}
