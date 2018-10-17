package visitor;

import java.util.HashMap;
import java.util.Map;

import ast.*;

public class EvalDoubleVisitor implements Visitor {

	private Map<String, Double> environment;
	private Object result; // Double or String for LValue

	public EvalDoubleVisitor(Map<String, Double> environment) {
		this.environment = environment;
	}

	public EvalDoubleVisitor() {
		this(new HashMap<>());
	}

	public Object getResult() {
		return result;
	}

	public Map<String, Double> getEnvironment() {
		return environment;
	}
	
	private void operation(OpExp exp) {
		exp.getLeft().accept(this);
		double r1 = (Double) result;
		exp.getRight().accept(this);
		double r2 = (Double) result;

		switch (exp.operator()) {
		case "+":
			result = r1 + r2;
			break;
		case "-":
			result = r1 - r2;
			break;
		case "*":
			result = r1 * r2;
			break;
		case "/":
			result = r1 / r2;
			break;
		case "%":
			result = r1 % r2;
			break;
		case "^":
			result = Math.pow(r1, r2);
			break;
		default:
			throw new UnsupportedOperationException("Unknown operator: " + exp.operator());
		}
	}

	@Override
	public void visit(PlusExp exp) {
		operation(exp);
	}

	@Override
	public void visit(MinusExp exp) {
		operation(exp);
	}

	@Override
	public void visit(TimesExp exp) {
		operation(exp);
	}

	@Override
	public void visit(DivExp exp) {
		operation(exp);
	}

	@Override
	public void visit(NumExp exp) {
		result = exp.getValue().doubleValue();
	}

	@Override
	public void visit(PowExp exp) {
		operation(exp);
	}

	@Override
	public void visit(RValueExp exp) {
		Double value = environment.get(exp.getName());
		if (value == null)
			throw new IllegalStateException("Invalid identifier: " + exp);

		result = value;
	}

	@Override
	public void visit(LValueExp exp) {
		result = exp.getName(); // key to retrieve the corresponding R-value
	}

	@Override
	public void visit(AssignExp exp) {
		exp.getLeft().accept(this);
		String key = (String) result;
		exp.getRight().accept(this);
		Double value = (Double) result;

		environment.put(key, value);
		// assignment as an expression whose value is the R-value
		// (value already set by the "right" accept)
	}

	@Override
	public void visit(UnaryMinusExp exp) {
		exp.getExp().accept(this);
		result = -1 * ((Double)result);
	}

	@Override
	public void visit(SeqExp exp) {
		exp.getLeft().accept(this);
		exp.getRight().accept(this);
		// sequence as an expression whose value is the one of the right-most expression
		// (value already set by the "right" accept)
	}

	@Override
	public void visit(ModExp exp) {
		operation(exp);
	}

}
