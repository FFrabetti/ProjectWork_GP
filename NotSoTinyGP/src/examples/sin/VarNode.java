package examples.sin;

import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class VarNode extends TerminalNode {

	private String name;
	
	public VarNode(String name) {
		this.name = name;
	}

	@Override
	public Node clone() {
		return new VarNode(name);
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this);
	}

	public String getName() {
		return name;
	}
	
}
