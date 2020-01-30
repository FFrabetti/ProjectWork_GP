package mulino;

public enum Checker {

	WHITE, BLACK, EMPTY;
	
	@Override
	public String toString() {
		return this.name().charAt(0) + "";
	}
	
}
