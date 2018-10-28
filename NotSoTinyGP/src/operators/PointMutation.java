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
		Node[] children = n.getChildren();
		int nChildren = children.length;
		
		if(RandomGenerator.getInstance().nextDouble() < pNode) {
			// debug
//			System.out.println("Mutating node " + n + " [" + n.getClass().getSimpleName() + "]");
			n = factory.getRandomNode(n.getArity());
		}
		
		// if the original node had children
		if(nChildren > 0) {
			Node[] newChildren = new Node[nChildren];
			for(int i=0; i<nChildren; i++)
				newChildren[i] = visitAndMutate(children[i]);
			n.setChildren(newChildren);
		}
		
		return n;
	}

}
