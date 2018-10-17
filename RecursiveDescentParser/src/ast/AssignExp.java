package ast;

import visitor.Visitor;

public class AssignExp extends OpExp {

	private static final String OP = "=";
	
	public AssignExp(Exp left, Exp right) {
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
