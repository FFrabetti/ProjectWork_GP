package mockimpl;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;
import utils.RandomGenerator;

public class MockFactory extends NodeFactory {

	private double p;
	
	public MockFactory(double p) {
		this.p = p;
	}
	
	@Override
	public Node getRandomNode() {
		return RandomGenerator.getInstance().nextDouble() < p ? getRandomTerminal() : getRandomFunction();
	}

	@Override
	public TerminalNode getRandomTerminal() {
		return new NumNode(4);
	}

	@Override
	public FunctionNode getRandomFunction() {
		return new OpNode();
	}

}
