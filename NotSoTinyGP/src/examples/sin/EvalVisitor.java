package examples.sin;

import java.util.Map;

import model.FunctionNode;
import model.TerminalNode;
import visitor.NodeVisitor;

public class EvalVisitor implements NodeVisitor {
	
	private double result;
	private Map<String,Double> env;
	
	public EvalVisitor(Map<String,Double> env) {
		this.env = env;
		result = 0;
	}
	
	public double getResult() {
		return result;
	}

	@Override
	public void visit(FunctionNode node) {
		OpNode opNode = (OpNode)node;

		opNode.getLeft().accept(this);;
		double t1 = getResult();
		opNode.getRight().accept(this);
		double t2 = getResult();
		
		switch(opNode.getOperator()) {
		case "+":
			result = t1 + t2;
			break;
		case "-":
			result = t1 - t2;
			break;
		case "*":
			result = t1 * t2;
			break;
		case "/": // protected division
			result = t2 != 0 ? t1 / t2 : 1;
			break;
		}
	}

	@Override
	public void visit(TerminalNode node) {
		if(node instanceof NumNode) {
			result = ((NumNode)node).getValue();
		}
		else if(node instanceof VarNode) { // right-value
			result = env.get(((VarNode)node).getName());
		}
	}
	
}