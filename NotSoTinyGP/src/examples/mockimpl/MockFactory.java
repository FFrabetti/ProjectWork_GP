package examples.mockimpl;

import java.util.Random;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;

public class MockFactory extends NodeFactory {

	private static final int N_TERMINALS = 10; // int [0, N_TERMINALS-1]
	
	private double pTerm;
	
	/*
	 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/22InitialisingthePopulation.html
	 * 
	 * The sizes and shapes of the trees generated via the grow method are highly
	 * sensitive to the sizes of the function and terminal sets:
	 * - significantly more terminals than functions -> short trees regardless of the depth limit
	 * - significantly more functions than terminals -> behavior similar to the full method.
	 */

	public MockFactory(Random random, double pTerm) {
		super(random);
		this.pTerm = pTerm;
		// on real cases p would be:
		// p = |terminal_set|/(|terminal_set|+|function_set|)
		// see NodeFactory.getRandomNode()
	}
	
	@Override
	public Node getRandomNode() {
		return super.getRandomNode(pTerm);
	}

	@Override
	public TerminalNode getRandomTerminal() {
		return new NumNode(random.nextInt(N_TERMINALS));
	}

	@Override
	public FunctionNode getRandomFunction() {
		return new OpNode(); // use setChildren() afterwards
	}

	@Override
	public Node getRandomNode(int arity) {
		return arity==0 ? getRandomTerminal() : getRandomFunction();
	}

	@Override
	public int getTerminalSetSize() {
		return N_TERMINALS;
	}

	@Override
	public int getFunctionSetSize() {
		return 1; // just OpNode, a "mock" binary operator
	}

}
