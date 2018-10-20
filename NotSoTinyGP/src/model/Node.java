package model;

import visitor.NodeVisitor;

public abstract class Node {
	
	protected static final Node[] EMPTY_NODES = new Node[0];
	
	
	public abstract int getArity();
	
	public abstract Node[] getChildren();
	
	public abstract void setChildren(Node[] children);
	
	public abstract void accept(NodeVisitor v);
	
}
