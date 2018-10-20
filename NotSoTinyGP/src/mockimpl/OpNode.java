package mockimpl;

import model.FunctionNode;
import model.Node;
import visitor.NodeVisitor;

public class OpNode extends FunctionNode {

	public OpNode() { }
	
	public OpNode(Node left, Node right) {
		setChildren(new Node[] {left, right});
	}
	
	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this);
	}
	
}
