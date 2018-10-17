package ast;

import visitor.Visitor;

public class UnaryMinusExp extends Exp {

	private Exp exp;
	
	public UnaryMinusExp(Exp exp) {
		this.exp = exp;
	}
	
	public Exp getExp() {
		return exp;
	}

	@Override
	public String toString() {
		return "-" + (exp instanceof AtomicExp ? exp : "(" + exp + ")");
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
