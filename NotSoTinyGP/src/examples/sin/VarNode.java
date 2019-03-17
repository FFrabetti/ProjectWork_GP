package examples.sin;

import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class VarNode extends TerminalNode {

	private String name;
	
	public VarNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public Node clone() {
		return new VarNode(name);
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this); // visit(TerminalNode)
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
