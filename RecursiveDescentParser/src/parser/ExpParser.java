package parser;

//import java.util.Stack;

public interface ExpParser<T> {

	public T parseExp();

	public T parseLine(String line);
	
//	public Stack<String> getDebugTrace();
	
}
