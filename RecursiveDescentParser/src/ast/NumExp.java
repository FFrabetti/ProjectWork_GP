package ast;

import visitor.Visitor;

public class NumExp extends AtomicExp {

	private Number value;

	public NumExp(Number value) {
		this.value = value;
	}
	
	public NumExp(String str) {
		try {
			value = Integer.parseInt(str);
		} catch(NumberFormatException e) {
			value = Double.parseDouble(str);
		}
	}

	public Number getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
