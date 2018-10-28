package operators;

import java.util.function.Function;

import model.Node;

public class DefaultOperator extends Operator {

	private Function<Node[],Node> function;
	
	public DefaultOperator(double opRate, Function<Node[],Node> function) {
		super(opRate);
		this.function = function;
	}

	@Override
	public Node apply(Node[] population) {
		return function.apply(population);
	}
	
}
