package visitor;

import java.util.HashMap;
import java.util.Map;

import ast.*;

public class EvalIntVisitor implements Visitor {

	private Map<String, Integer> environment;
	private Object result = null; // Integer or String for LValue

	public EvalIntVisitor(Map<String, Integer> environment) {
		this.environment = environment;
	}

	public EvalIntVisitor() {
		this(new HashMap<>());
	}

	public Object getResult() {
		return result;
	}

	public Map<String, Integer> getEnvironment() {
		return environment;
	}
	
	private void operation(OpExp exp) {
		exp.getLeft().accept(this);
		Integer r1 = (Integer) result;
		exp.getRight().accept(this);
		Integer r2 = (Integer) result;

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
			result = r1 / r2; // if r2 == 0 throws exception
			break;
		case "%":
			result = r1 % r2;
			break;
		case "^":
			double pow = Math.pow(r1, r2);
			// Math.rint rounds ties (.5) to the closest EVEN integer
			// positive and negative numbers are treated symmetrically
			// https://en.wikipedia.org/wiki/Rounding#Round_half_to_even
			result = (int) Math.rint(pow); // Math.round
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
		result = exp.getValue();
	}

	@Override
	public void visit(PowExp exp) {
		operation(exp);
	}

	@Override
	public void visit(RValueExp exp) {
		Integer value = environment.get(exp.getName());
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
		Integer value = (Integer) result;

		environment.put(key, value);
		// assignment as an expression whose value is the R-value
		// (value already set by the "right" accept)
	}

	@Override
	public void visit(UnaryMinusExp exp) {
		exp.getExp().accept(this);
		result = -(int)result;
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
