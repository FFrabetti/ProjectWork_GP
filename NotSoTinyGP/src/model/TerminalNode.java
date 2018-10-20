package model;

public abstract class TerminalNode extends Node {

	@Override
	public int getArity() {
		return 0;
	}

	@Override
	public Node[] getChildren() {
		return EMPTY_NODES;
	}
	
	@Override
	public void setChildren(Node[] children) {
		throw new RuntimeException("TerminalNode setChildren");
	}

}
