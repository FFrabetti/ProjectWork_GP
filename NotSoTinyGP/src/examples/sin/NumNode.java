package examples.sin;

import java.text.DecimalFormat;

import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class NumNode extends TerminalNode {

	private static final DecimalFormat formatter = new DecimalFormat("0.##");
	
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
	
	@Override
	public String toString() {
		return formatter.format(value);
	}
	
}
