package examples.mockimpl;

import fitness.FitnessFunction;
import model.FunctionNode;
import model.Node;
import model.TerminalNode;
import visitor.NodeVisitor;

/*
 * Fitness evaluation is usually very expensive, so consider storing the results for each generation in a hash-map.
 * This avoids multiple evaluations for the same individual.
 */

public class MockFitness implements FitnessFunction, NodeVisitor {

	private int nrEven;
	private int nrLeaves;
	
	// fitness = nr of even numbers/tot number of leaves
	// 0 <= fitness <= 1
	@Override
	public double evalFitness(Node n) {
		nrEven = 0;
		nrLeaves = 0;
		
		n.accept(this);
		
		return nrEven/(float)nrLeaves; // ok: nrLeaves always != 0
	}

	@Override
	public void visit(FunctionNode node) {
		for(Node child : node.getChildren())
			child.accept(this);
	}

	@Override
	public void visit(TerminalNode node) {
		if(node instanceof NumNode) {
			nrLeaves++;
			if(((NumNode)node).getValue() % 2 == 0)
				nrEven++;
		}
	}
	
}
