package model;

public abstract class FunctionNode extends Node {

	private Node[] children;
	
	public FunctionNode() {
		// to be used when you want to create an empty FunctionNode before its children
		// NOTE: the check in getChildren() enforces consistency
	}
	
	public FunctionNode(Node[] children) {
		setChildren(children);
	}
	
	@Override
	public Node[] getChildren() {
		return children != null ? children : EMPTY_NODES;
	}
	
	// do NOT edit the children array (from getChildren()) without calling setChildren afterwards!
	@Override
	public void setChildren(Node[] children) {
		if(children == null)
			throw new IllegalArgumentException("children == null");
		if(children.length != getArity())
			throw new IllegalArgumentException("children.length != arity");

		this.children = children;
		// bidirectional link
		for(Node child : children)
			child.setParent(this);
	}
	
}
