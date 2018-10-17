package ast;

abstract public class OpExp extends Exp {
	
	private Exp left, right;

	public OpExp(Exp left, Exp right) {
		this.left = left;
		this.right = right;
	}

	public Exp getLeft() {
		return left;
	}

	public Exp getRight() {
		return right;
	}
	
	public abstract String operator();
	
	@Override
	public String toString() {
//		return left + operator() + right;
		return	(left instanceof AtomicExp ? left : "(" + left + ")")
				+ operator()
				+ (right instanceof AtomicExp ? right : "(" + right + ")");
	}
	
}
