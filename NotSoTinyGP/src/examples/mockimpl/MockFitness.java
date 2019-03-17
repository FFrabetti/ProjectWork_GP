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
	private int target;
	
	private int nrEven;		// nr of even numbers (NumNode.getValue())
	private int nrLeaves;	// total nr of leaves/terminals (NumNode)
	
	public MockFitness(int target) {
		this.target = target;
	}
	
	public MockFitness() {
		this(DEF_TARGET);
	}
	
	// dist = |target-nrLeaves|
	// dist is [0,+inf]
	// alpha = 1		(if dist==0)
	// alpha -> 0	(if dist!=0, dist->+inf)
	// fitness = nrEven/nrLeaves * alpha = nrEven/nrLeaves * e^(-dist)
	// 0 <= fitness <= 1
	// the "perfect" tree has exactly <target> leaves, all containing even numbers
	@Override
	public double evalFitness(Node n) {
		nrEven = 0;
		nrLeaves = 0;
		
		n.accept(this);
		
		double alpha = Math.exp(-Math.abs(target-nrLeaves));
		return alpha * nrEven/nrLeaves; // ok: nrLeaves always != 0 (at least the root)
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
