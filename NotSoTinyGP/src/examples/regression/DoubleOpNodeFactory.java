package examples.regression;

import java.util.Random;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B1OverviewofTinyGP.html
 * 
 * 1.	The terminal set includes a user-definable number of floating point variables (named X1 to XN).
 * 2.	The function set includes multiplication, protected division, subtraction and addition.
 */

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B2InputDataFilesforTinyGP.html
 * 
 * NVAR is an integer representing the number of variables the system should use
 * NRAND is an integer representing the number of random constants to be provided in the primitive set
 * MINRAND is a float representing the lower limit of the range used to generate random constants
 * MAXRAND is the corresponding upper limit
 * 
 * NRAND can be set to 0, in which case MINRAND and MAXRAND are ignored. 
 */

public class DoubleOpNodeFactory extends NodeFactory {
	
	private double pTerm;
	private TerminalNode[] terminals;
	private Class[] functions;
	
	public DoubleOpNodeFactory(Random random, int nvar, int nrand, double minrand, double maxrand, double pTerm) {
		super(random);
		this.pTerm = pTerm;
		
		terminals = new TerminalNode[nvar + nrand];
		for(int i=0; i<nvar; i++)
			terminals[i] = new VarNode("x"+i);
		for(int i=nvar; i<nrand+nvar; i++)
			terminals[i] = new DoubleNode(random.nextDouble()*(maxrand-minrand)+minrand);
		
		functions = new Class[] {
			PlusNode.class,
			MinusNode.class,
			TimesNode.class,
			DivNode.class	
		};
	}

	public DoubleOpNodeFactory(Random random, int nvar, int nrand, double minrand, double maxrand) {
		this(random, nvar, nrand, minrand, maxrand, 0.5); // 50% prob. of selecting a terminal
	}
	
	@Override
	public Node getRandomNode() {
		// p = |terminal_set|/(|terminal_set|+|function_set|)
		// nTerminals >> functions.length !
		return super.getRandomNode(pTerm);
	}

	@Override
	public TerminalNode getRandomTerminal() {
		return (TerminalNode) terminals[random.nextInt(terminals.length)].clone();
	}

	@Override
	public FunctionNode getRandomFunction() {
		try {
			return (FunctionNode) functions[random.nextInt(functions.length)].newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Node getRandomNode(int arity) {
		return arity==0 ? getRandomTerminal() : getRandomFunction();
	}

	@Override
	public int getTerminalSetSize() {
		return terminals.length;
	}

	@Override
	public int getFunctionSetSize() {
		return functions.length;
	}

}
