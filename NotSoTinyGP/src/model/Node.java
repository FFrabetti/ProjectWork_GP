package model;

import visitor.NodeVisitor;

public abstract class Node implements Cloneable {
	
	protected static final Node[] EMPTY_NODES = new Node[0];
	
	// the root has parent == null
	private Node parent;
	
	// ATTENTION!
	// be VERY careful with parent-children pointers consistency!
	protected void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public void makeRoot() {
		parent = null;
	}

	@Override
	public abstract Node clone();
	
	public abstract int getArity();
	
	public abstract Node[] getChildren();
	
	public abstract void setChildren(Node[] children);
	
	public abstract void accept(NodeVisitor v);
	
}
