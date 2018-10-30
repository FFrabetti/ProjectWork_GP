package visitor;

import model.FunctionNode;
import model.TerminalNode;

public interface NodeVisitor {

	public void visit(FunctionNode node);
	public void visit(TerminalNode node);
	
}
