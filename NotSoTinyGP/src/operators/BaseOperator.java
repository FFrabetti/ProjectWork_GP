package operators;

import java.util.function.Function;

import model.Node;

// utility class for rapid operator prototyping
public class BaseOperator extends Operator {

	private Function<Node[],Node> function;
	
	public BaseOperator(double opRate, Function<Node[],Node> function) {
		super(opRate);
		this.function = function;
	}

	@Override
	public Node apply(Node[] population) {
		return function.apply(population);
	}
	
}
