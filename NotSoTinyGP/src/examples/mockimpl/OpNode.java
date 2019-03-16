package examples.mockimpl;

import model.FunctionNode;
import model.Node;
import visitor.NodeVisitor;

public class OpNode extends FunctionNode {

	OpNode() {
		// used by MockFactory (remember to call setChildren() right afterward)
	}
	
	public OpNode(Node left, Node right) {
		setChildren(new Node[] {left, right});
	}
	
	public Node getLeft() {
		return getChildren()[0];
	}
	
	public Node getRight() {
		return getChildren()[1];
	}
	
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
		Node[] c = getChildren();
		return "(" + c[0] + "," + c[1] + ")";
	}

	@Override
	public Node clone() {
		return new OpNode(getLeft().clone(), getRight().clone());
//		result.setParent(this.getParent());
		// the constructor calls FunctionNode.setChildren(), which does setParent() on each child
	}
	
}
