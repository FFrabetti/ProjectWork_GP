package ast;

import visitor.Visitor;

public class SeqExp extends OpExp {

	private static final String OP = ",";
	
	public SeqExp(Exp left, Exp right) {
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
