package mockimpl;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;
import utils.RandomGenerator;

public class MockFactory extends NodeFactory {

	private double p;
	
	/*
	 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/22InitialisingthePopulation.html
	 * 
	 * The sizes and shapes of the trees generated via the grow method are highly
	 * sensitive to the sizes of the function and terminal sets:
	 * - significantly more terminals than functions -> short trees regardless of the depth limit
	 * - significantly more functions than terminals -> behavior similar to the full method.
	 */

	public MockFactory(double p) {
		this.p = p;
		// on real cases p would be:
		// p = |terminal_set|/(|terminal_set|+|function_set|)
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
