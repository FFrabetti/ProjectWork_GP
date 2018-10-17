package ast;

import visitor.Visitor;

public class TimesExp extends OpExp {

	private static final String OP = "*";
	
	public TimesExp(Exp left, Exp right) {
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
