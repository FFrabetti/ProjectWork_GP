package ast;

import visitor.Visitor;

public class LValueExp extends AtomicExp {

	private String name;
	
	public LValueExp(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
