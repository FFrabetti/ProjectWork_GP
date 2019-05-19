package model;

import visitor.NodeVisitor;

public abstract class Node implements Cloneable {
	
	protected static final Node[] EMPTY_NODES = new Node[0];
	
	// the root has parent == null
	private Node parent;
	
	Node() {
		/* package protected constructor:
		 * it prevents direct subclassing (except within the same package)
		 * implementation-specific node classes have to extend either FunctionNode or TerminalNode!
		 */
	}
	
	// ATTENTION!
	// be VERY careful with parent-children pointers consistency!
	// method used by setChildren(Node[])
	protected void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	// it should almost never be necessary to use it...
	public void makeRoot() {
		parent = null;
	}

	@Override
	// A class implements the Cloneable interface to indicate to the java.lang.Object.clone() method
	// that it is legal for that method to make a field-for-field copy of instances of that class.
	public abstract Node clone();
	
	public abstract int getArity();
	
	public abstract Node[] getChildren();
	
	public abstract void setChildren(Node[] children);
	
	public abstract void accept(NodeVisitor visitor);
	
}
