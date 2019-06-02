package examples.mockimpl;

import model.FunctionNode;
import model.Node;
import model.TerminalNode;
import selection.FitnessFunction;
import visitor.NodeVisitor;

/*
 * Fitness evaluation is - in real scenarios - usually very expensive,
 * so consider storing the results for each generation in a hash-map.
 * This avoids multiple evaluations for the same individual.
 */

public class MockFitness implements FitnessFunction, NodeVisitor {

	private static final int DEF_TARGET = 16;
	
	private int target;		// desired number of leaves
	private int nrEven;		// nr of even numbers (NumNode.getValue())
	private int nrLeaves;	// total nr of leaves/terminals (NumNode)
	
	public MockFitness(int target) {
		this.target = target;
	}
	
	public MockFitness() {
		this(DEF_TARGET);
	}
	
	// dist = |target-nrLeaves|
	// 		dist is in [0,+inf[ -> -dist is in ]-inf,0]
	// alpha = e^(-dist)
	// 		alpha = 1	(if dist == 0)
	// 		alpha -> 0	(if dist -> +inf)
	
	// beta = nrEven/nrLeaves
	// 		beta is in [0,1], max when all leaves are even
	
	// fitness = alpha * beta = e^(-dist) * nrEven/nrLeaves
	// 		0 <= fitness <= 1
	// the "perfect" tree has exactly <target> leaves, all containing even numbers
	
	// variant: balance even and odd numbers
	// beta = 1 - 2*|0.5 - nrEven/nrLeaves|
	//		beta is in [0,1]
	@Override
	public double evalFitness(Node n) {
		nrEven = 0;
		nrLeaves = 0;
		
		n.accept(this);
		
		double alpha = Math.exp(-Math.abs(target-nrLeaves));
		double beta = nrEven/(float)nrLeaves; // nrLeaves always != 0 (at least 1, the root)
//		beta = 1 - 2*Math.abs(0.5 - beta);
		return alpha * beta;
	}

	@Override
	public void visit(FunctionNode node) {
		for(Node child : node.getChildren())
			child.accept(this);
	}

	@Override
	public void visit(TerminalNode node) {
		if(node instanceof NumNode) { // it should always be true
			nrLeaves++;
			
			if(((NumNode)node).getValue() % 2 == 0)
				nrEven++;
		}
	}

	@Override
	public double maxFitness() {
		return 1;
	}
	
}
