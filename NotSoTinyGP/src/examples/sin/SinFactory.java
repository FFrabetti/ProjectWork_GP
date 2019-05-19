package examples.sin;

import java.util.ArrayList;
import java.util.List;
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

public class SinFactory extends NodeFactory {
	
	private int nTerminals; // utility field (== terminals.size())
	private List<TerminalNode> terminals;
	
	private static List<Class<? extends FunctionNode>> functions = new ArrayList<>();
	
	static {
		functions.add(PlusNode.class);
		functions.add(MinusNode.class);
		functions.add(TimesNode.class);
		functions.add(DivNode.class);
	}
	
	public SinFactory(Random random, int nvar, int nrand, double minrand, double maxrand) {
		super(random);
		
		nTerminals = nvar + nrand;
		terminals = new ArrayList<>(nTerminals);
		
		for(int i=0; i<nvar; i++)
			terminals.add(new VarNode("x"+i));
		
		for(int i=0; i<nrand; i++)
			terminals.add(new DoubleNode(random.nextDouble()*(maxrand-minrand)+minrand));
	}

	@Override
	public Node getRandomNode() {
		// p = |terminal_set|/(|terminal_set|+|function_set|)
		// nTerminals >> functions.length !
		// it uses a 50% prob. of selecting a terminal
		return super.getRandomNode(0.5);
	}

	@Override
	public TerminalNode getRandomTerminal() {
		return (TerminalNode) terminals.get(random.nextInt(nTerminals)).clone();
	}

	@Override
	public FunctionNode getRandomFunction() {
		try {
			return functions.get(random.nextInt(functions.size())).newInstance();
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
		return nTerminals;
	}

	@Override
	public int getFunctionSetSize() {
		return functions.size();
	}

}
