package operators;

import model.Node;
import model.NodeFactory;
import utils.RandomGenerator;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/24RecombinationandMutation.html
 * 
 * In P.M., a random node is selected and the primitive stored there is replaced with a random primitive of the same arity.
 * Each node is considered in turn and, with a certain probability, it is altered as explained above.
 */

public class PointMutation implements Mutation {

	private NodeFactory factory;
	private double pNode; // probability of a single node to be mutated
	
	public PointMutation(NodeFactory factory, double pNode) {
		this.factory = factory;
		this.pNode = pNode;
	}

	@Override
	public Node mutate(Node n) {
		return visitAndMutate(n.clone());
	}

	private Node visitAndMutate(Node n) {
		boolean mutation = RandomGenerator.getInstance().nextDouble() < pNode;
		
		if(n.getArity() == 0) // terminal
			return mutation ? factory.getRandomTerminal() : n;
		else {
			Node[] children = n.getChildren();
			for (int i = 0; i < children.length; i++)
				children[i] = visitAndMutate(children[i]);
			// now children contains the "mutated" child-nodes
			
			if(mutation)
				n = factory.getRandomNode(n.getArity());
			// n is replaced by a new function node with the same arity
			
			n.setChildren(children); // fix parent-child bidirectional link
			return n;
		}
	}

}
