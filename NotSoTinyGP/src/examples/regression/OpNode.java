package examples.regression;

import model.FunctionNode;
import model.Node;
import visitor.NodeVisitor;

public abstract class OpNode extends FunctionNode {
	
	public OpNode(Node left, Node right) {
		super(new Node[] {left, right});
	}

	OpNode() {
		// used by the Factory (remember to call setChildren() right afterward)
	}
	
	public Node getLeft() {
		return getChildren()[0];
	}
	
	public Node getRight() {
		return getChildren()[1];
	}

	public abstract String getOperator();
	
//	@Override
//	public Node clone() {
//		return new OpNode(op, getLeft().clone(), getRight().clone());
//	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this); // visit(FunctionNode)
	}

	@Override
	public String toString() {
		// prefix notation: no need for brackets
		return getOperator() + " " + getLeft() + " " + getRight();
	}
	
}
