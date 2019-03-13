package model;

import utils.RandomGenerator;

public abstract class NodeFactory {

	public abstract int getTerminalSetSize();
	
	public abstract int getFunctionSetSize();
	
	public abstract TerminalNode getRandomTerminal();
	
	public abstract FunctionNode getRandomFunction();

	public Node getRandomNode() {
		int tss = getTerminalSetSize();
		int tot = tss + getFunctionSetSize();
		
		return getRandomNode(tss/(double)tot);
	}
	
	public Node getRandomNode(double termProb) {
		return RandomGenerator.getInstance().nextDouble() < termProb ? getRandomTerminal() : getRandomFunction();
	}
	
	public abstract Node getRandomNode(int arity);
	
}
