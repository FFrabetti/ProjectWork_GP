package model;

import java.util.Random;

public abstract class NodeFactory {
	
	// easy access from sub-classes
	protected final Random random;
	
	public NodeFactory(Random random) {
		this.random = random;
	}

	public abstract int getTerminalSetSize();
	
	public abstract int getFunctionSetSize();
	
	public abstract TerminalNode getRandomTerminal();
	
	public abstract FunctionNode getRandomFunction();

	// NOTE: use getRandomNode(double) when |terminal set| >> |function set| 
	public Node getRandomNode() {
		int tss = getTerminalSetSize();
		int tot = tss + getFunctionSetSize();
		
		return getRandomNode(tss/(double)tot);
	}
	
	public Node getRandomNode(double pTerm) {
		return random.nextDouble() < pTerm ? getRandomTerminal() : getRandomFunction();
	}
	
	public abstract Node getRandomNode(int arity);
	
}
