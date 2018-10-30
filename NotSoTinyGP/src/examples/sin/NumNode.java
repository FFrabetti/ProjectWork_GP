package examples.sin;

import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class NumNode extends TerminalNode {

	private double value;
	
	public NumNode(double value) {
		this.value = value;
	}

	@Override
	public Node clone() {
		return new NumNode(value);
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this);
	}

	public double getValue() {
		return value;
	}
	
}
