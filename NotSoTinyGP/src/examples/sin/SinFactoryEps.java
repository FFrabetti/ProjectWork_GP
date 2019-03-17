package examples.sin;

import java.util.ArrayList;
import java.util.List;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;
import utils.RandomGenerator;

/*
 * The idea is to fill the terminal set with nrand "epsilon" fictitious terminals:
 * every time one of them is selected, a new DoubleNode is generated (in [minrand,maxrand[).
 * This overcomes the problem of imbalance between the number of variables (usually small)
 * and the number or random constants (nrand) in the terminal set (usually high).
 * 
 * nvar and nrand are still to be used to tune the probability of selecting a terminal
 * of one type or the other, as all "epsilon" terminals are perfectly equivalent.
 */

public class SinFactoryEps extends NodeFactory {
	
	private RandomGenerator rand = RandomGenerator.getInstance();
	
	private int nTerminals;
	private List<TerminalNode> vars;
	
	private double minrand;
	private double maxrand;
	
	private static List<Class<? extends FunctionNode>> functions = new ArrayList<>();
	
	static {
		functions.add(PlusNode.class);
		functions.add(MinusNode.class);
		functions.add(TimesNode.class);
		functions.add(DivNode.class);
	}
	
	public SinFactoryEps(int nvar, int nrand, double minrand, double maxrand) {
		this.minrand = minrand;
		this.maxrand = maxrand;
		
		nTerminals = nvar + nrand;
		vars = new ArrayList<>(nvar); // just variables!
		for(int i=0; i<nvar; i++)
			vars.add(new VarNode("x"+i));
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
		int index = rand.nextInt(nTerminals);
		return index<vars.size() ? (TerminalNode)vars.get(index).clone() : getRandomDouble();
	}

	private DoubleNode getRandomDouble() {
		return new DoubleNode(rand.nextDouble()*(maxrand-minrand)+minrand);
	}
	
	@Override
	public FunctionNode getRandomFunction() {
		try {
			return functions.get(rand.nextInt(functions.size())).newInstance();
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
