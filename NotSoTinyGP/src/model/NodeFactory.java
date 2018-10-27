package model;

public abstract class NodeFactory {

	public abstract Node getRandomNode();
	
	public abstract TerminalNode getRandomTerminal();
	
	public abstract FunctionNode getRandomFunction();

	public abstract Node getRandomNode(int arity);
	
}
