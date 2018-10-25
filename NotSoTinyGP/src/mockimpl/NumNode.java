package mockimpl;

import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class NumNode extends TerminalNode {

	private int num;
	
	public NumNode(int num) {
		this.num = num;
	}

	public int getValue() {
		return num;
	}
	
	@Override
	public void accept(NodeVisitor v) {
		v.visit(this);
	}
	
	@Override
	public String toString() {
		return String.valueOf(num);
	}

	@Override
	public Node clone() {
		return new NumNode(getValue());
	}
	
}
