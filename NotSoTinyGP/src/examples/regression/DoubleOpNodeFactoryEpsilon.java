package examples.regression;

import java.util.Random;

import model.TerminalNode;

/*
 * The idea is to fill the terminal set with nrand "epsilon" fictitious terminals:
 * every time one of them is selected, a new DoubleNode is generated (in [minrand,maxrand[).
 * This overcomes the problem of imbalance between the number of variables (usually small)
 * and the number or random constants (nrand) in the terminal set (usually high).
 * 
 * nvar and nrand are still to be used to tune the probability of selecting a terminal
 * of one type or the other, as all "epsilon" terminals are perfectly equivalent.
 */

public class DoubleOpNodeFactoryEpsilon extends DoubleOpNodeFactory {
	
	// nvar = getTerminalSetSize()
	private int nrand;
	private double minrand;
	private double maxrand;
	
	public DoubleOpNodeFactoryEpsilon(Random random, int nvar, int nrand, double minrand, double maxrand, double pTerm) {
		super(random, nvar, 0, minrand, maxrand, pTerm); // 0 fixed random constants
		this.nrand = nrand;
		this.minrand = minrand;
		this.maxrand = maxrand;
	}
	
	public DoubleOpNodeFactoryEpsilon(Random random, int nvar, int nrand, double minrand, double maxrand) {
		this(random, nvar, nrand, minrand, maxrand, 0.5); // 50% prob. of selecting a terminal
	}

	@Override
	public TerminalNode getRandomTerminal() {
		int index = random.nextInt(getTerminalSetSize() + nrand);
		return index<nrand ? getRandomDouble() : super.getRandomTerminal();
	}

	private DoubleNode getRandomDouble() {
		return new DoubleNode(random.nextDouble()*(maxrand-minrand)+minrand);
	}
	
}
