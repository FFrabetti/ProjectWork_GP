package visitor;

import ast.*;

public interface Visitor {

	public void visit(PlusExp exp);
	public void visit(MinusExp exp);
	public void visit(TimesExp exp);
	public void visit(DivExp exp);
	public void visit(PowExp exp);
	public void visit(ModExp exp);
	
	public void visit(NumExp exp);
	
	public void visit(RValueExp exp);
	public void visit(LValueExp exp);
	public void visit(AssignExp exp);
	
	public void visit(UnaryMinusExp exp);
	
	public void visit(SeqExp exp);
	
}
