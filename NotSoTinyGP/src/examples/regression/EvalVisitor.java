package examples.regression;

import java.util.Map;

import model.FunctionNode;
import model.TerminalNode;
import visitor.NodeVisitor;

// REMEMBER: the behavior of double overflow is to flush to +/-infinity
// (e.g. Double.MAX_VALUE == Double.MAX_VALUE+1000)

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
		if(!(node instanceof OpNode))
			throw new IllegalArgumentException("expected an instance of OpNode");
		
		OpNode opNode = (OpNode)node;

		opNode.getLeft().accept(this);;
		double t1 = getResult();
		opNode.getRight().accept(this);
		double t2 = getResult();
		
		if(opNode instanceof PlusNode)
			visitOp((PlusNode)opNode, t1, t2);
		else if(opNode instanceof MinusNode)
			visitOp((MinusNode)opNode, t1, t2);
		else if(opNode instanceof TimesNode)
			visitOp((TimesNode)opNode, t1, t2);
		else if(opNode instanceof DivNode)
			visitOp((DivNode)opNode, t1, t2);
		
//		switch(opNode.getOperator()) {
//		case "+":
//			result = t1 + t2;
//			break;
//		case "-":
//			result = t1 - t2;
//			break;
//		case "*":
//			result = t1 * t2;
//			break;
//		case "/": // protected division
//			result = t2 != 0 ? t1 / t2 : 1;
//			break;
//		}
	}

	private void visitOp(PlusNode n, double t1, double t2) {
		result = t1 + t2;
	}
	
	private void visitOp(MinusNode n, double t1, double t2) {
		result = t1 - t2;
	}
	
	private void visitOp(TimesNode n, double t1, double t2) {
		result = t1 * t2;
	}
	
	// protected division
	private void visitOp(DivNode n, double t1, double t2) {
//		result = t2 != 0 ? t1/t2 : 1;
		
		// method used by TinyGP
		result = Math.abs(t2) <= 0.001 ? t1*signum(t2) : t1/t2;
		// ratio: the division by "zero" is ignored
		// DivNode(n,+/-0) equiv. DoubleNode(+/-n)
	}
	
	// Math.signum(0) == 0, which is undesirable
	private int signum(double x) {
		return x<0 ? -1 : 1;
	}
	
	@Override
	public void visit(TerminalNode node) {
		if(node instanceof DoubleNode)
			result = ((DoubleNode)node).getValue();
		else if(node instanceof VarNode) { // right-value
			String key = ((VarNode)node).getName();
			if(!env.containsKey(key))
				throw new IllegalArgumentException("undefined variable " + key);
			
			result = env.get(key);
		}
		else
			throw new IllegalArgumentException("unknown instance of TerminalNode");
	}
	
}
