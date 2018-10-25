package model;

public abstract class FunctionNode extends Node {

	private Node[] children;
	
	public FunctionNode() { }
	
	public FunctionNode(Node[] children) {
		setChildren(children);
	}
	
	@Override
	public Node[] getChildren() {
		return children != null ? children : EMPTY_NODES;
	}
	
	// do NOT edit Node[] children without calling setChildren afterwards
	@Override
	public void setChildren(Node[] children) {
		if(children == null)
			throw new IllegalArgumentException("children == null");
		if(children.length != getArity())
			throw new IllegalArgumentException("children.length != arity");

		this.children = children;		
		for(Node child : children)
			child.setParent(this);
	}
	
}
