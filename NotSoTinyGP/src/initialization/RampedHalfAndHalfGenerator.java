package initialization;

import model.Node;
import model.NodeFactory;
import utils.RandomGenerator;

public class RampedHalfAndHalfGenerator extends PopulationGenerator {

	private int minDepth;
	private int offset; // maxDepth = minDepth + offset
	private double probFull; // probGrow = 1 - probFull
	
	private RandomGenerator rnd = RandomGenerator.getInstance();
	
	public RampedHalfAndHalfGenerator(NodeFactory nodeFactory, int minDepth, int maxDepth, double probFull) {
		super(nodeFactory);
		
		this.minDepth = minDepth;
		offset = maxDepth-minDepth;
		this.probFull = probFull;
	}

	public RampedHalfAndHalfGenerator(NodeFactory nodeFactory, int minDepth, int maxDepth) {
		this(nodeFactory, minDepth, maxDepth, 0.5); // half-and-half
	}
	
	@Override
	public Node[] generate(int size) {
		Node[] population = new Node[size];
		
		for(int i=0; i<size; i++) { // ramped: variable depth
			// offset+1 because the upper bound is exclusive
			int depth = offset>0 ? minDepth + rnd.nextInt(offset+1) : minDepth;
			population[i] = newIndividual(depth);
		}
		
		return population;
	}

	private Node newIndividual(int depth) {
		if(depth == 0)
			return getFactory().getRandomTerminal();
		else {
			// full or grow method
			Node node = rnd.nextDouble() < probFull ? getFactory().getRandomFunction() : getFactory().getRandomNode();
			
			int nChildren = node.getArity();
			if(nChildren > 0) {
				Node[] children = new Node[nChildren];
				for(int i=0; i<nChildren; i++)
					children[i] = newIndividual(depth-1);
				node.setChildren(children);
			}
			
			return node;
		}
	}
	
}
