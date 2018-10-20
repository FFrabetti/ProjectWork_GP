package model;

public abstract class FunctionNode extends Node {

	private Node[] children;
	
	public FunctionNode() { }
	
	public FunctionNode(Node[] children) {
		this.children = children;
	}
	
	@Override
	public Node[] getChildren() {
		return children != null ? children : EMPTY_NODES;
	}
	
	@Override
	public void setChildren(Node[] children) {
		this.children = children;
	}
	
}
