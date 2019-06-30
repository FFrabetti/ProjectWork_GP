package examples.regression;

import model.Node;

public class TimesNode extends OpNode {

	private static final String OP = "*";
	
	public TimesNode(Node left, Node right) {
		super(left, right);
	}
	
	TimesNode() {}
	
	@Override
	public String getOperator() {
		return OP;
	}

	@Override
	public Node clone() {
		return new TimesNode(getLeft().clone(), getRight().clone());
	}

}
