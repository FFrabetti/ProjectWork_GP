package ast;

import visitor.Visitor;

public class PowExp extends OpExp {

	private static final String OP = "^";
	
	public PowExp(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public String operator() {
		return OP;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
	
}
