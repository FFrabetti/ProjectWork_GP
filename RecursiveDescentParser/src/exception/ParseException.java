package exception;

public class ParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ParseException(String string) {
		super(string);
	}
	
	public ParseException(Exception e) {
		super(e);
	}

}
