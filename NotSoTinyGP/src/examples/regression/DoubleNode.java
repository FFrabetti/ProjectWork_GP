package examples.regression;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

public class DoubleNode extends TerminalNode {

	// nice toString()
	public static final DecimalFormat formatter; // = new DecimalFormat("0.##");
	
	static {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		formatter = (DecimalFormat)nf;
		formatter.applyPattern("0.##");
	}
	
	private double value;
	
	public DoubleNode(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
	
	@Override
	public Node clone() {
		return new DoubleNode(value);
	}

	@Override
	public void accept(NodeVisitor v) {
		v.visit(this); // visit(TerminalNode)
	}
	
	@Override
	public String toString() {
		return formatter.format(value);
	}
	
}
