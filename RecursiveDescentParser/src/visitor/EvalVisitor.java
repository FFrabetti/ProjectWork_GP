package visitor;

import java.util.HashMap;
import java.util.Map;

import ast.*;

public class EvalVisitor implements Visitor {

	// Number can be Integer (whenever possible) or Double
	// different semantic for / (and % ?) according to the actual type of the operands (both Integer or else)
	
	private Map<String, Number> environment;
	private Object result; // Number or String for LValue

	public EvalVisitor(Map<String, Number> environment) {
		this.environment = environment;
	}

	public EvalVisitor() {
		this(new HashMap<>());
	}

	public Object getResult() {
		return result;
	}

	public Map<String, Number> getEnvironment() {
		return environment;
	}
	
	private void operation(OpExp exp) {
		exp.getLeft().accept(this);
		Number n1 = (Number) result;
		exp.getRight().accept(this);
		Number n2 = (Number) result;

		if(n1 instanceof Integer && n2 instanceof Integer) {
			intOperation(n1.intValue(), exp.operator(), n2.intValue());
			return;
		}
		
		double r1 = n1.doubleValue();
		double r2 = n2.doubleValue();
		
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

	private void intOperation(int i1, String operator, int i2) {
		switch (operator) {
		case "+":
			result = i1 + i2;
			break;
		case "-":
			result = i1 - i2;
			break;
		case "*":
			result = i1 * i2;
			break;
		case "/":
			result = i1 / i2;
			break;
		case "%":
			result = i1 % i2;
			break;
		case "^": // the result can be a double when i2<0
			double pow = Math.pow(i1, i2);
			if(Math.rint(pow) == pow)
				result = (int)pow;
			else
				result = pow;
			break;
		default:
			throw new UnsupportedOperationException("Unknown operator: " + operator);
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
		Number value = environment.get(exp.getName());
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
		Number value = (Number) result;

		environment.put(key, value);
		// assignment as an expression whose value is the R-value
		// (value already set by the "right" accept)
	}

	@Override
	public void visit(UnaryMinusExp exp) {
		exp.getExp().accept(this);
		if(result instanceof Integer)
			result = -1 * (Integer)result;
		else
			result = -1 * ((Number)result).doubleValue();
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
