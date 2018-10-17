package ast;

import visitor.Visitor;

public class RValueExp extends AtomicExp {

	private String name;
	
	public RValueExp(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "$" + name;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
