package examples.regression;

import model.Node;

public class MinusNode extends OpNode {

	private static final String OP = "-";
	
	public MinusNode(Node left, Node right) {
		super(left, right);
	}
	
	MinusNode() {}
	
	@Override
	public String getOperator() {
		return OP;
	}

	@Override
	public Node clone() {
		return new MinusNode(getLeft().clone(), getRight().clone());
	}

}
