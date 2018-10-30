package examples.sin;

import model.FunctionNode;
import model.Node;
import visitor.NodeVisitor;

public class OpNode extends FunctionNode {

	private String op;
	
	public OpNode(String op, Node left, Node right) {
		super(new Node[] {left, right});
		this.op = op;
	}

	public OpNode(String op) {
		this.op = op;
	}
	
	@Override
	public Node clone() {
		return new OpNode(op, getLeft().clone(), getRight().clone());
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this);
	}

	public String getOperator() {
		return op;
	}
	
	public Node getLeft() {
		return getChildren()[0];
	}
	
	public Node getRight() {
		return getChildren()[1];
	}
	
}
