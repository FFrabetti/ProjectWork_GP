package operators;

import model.Node;

public abstract class Operator {

	private double opRate; // probability of application
	
	public Operator(double opRate) {
		this.opRate = opRate;
	}

	public double getOperatorRate() {
		return opRate;
	}

	public abstract Node apply(Node[] population);

}
