package mockimpl;

import model.TerminalNode;
import visitor.NodeVisitor;

public class NumNode extends TerminalNode {

	private int num;
	
	public NumNode(int num) {
		this.num = num;
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this);
	}
	
}
