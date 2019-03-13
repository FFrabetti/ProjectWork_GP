package initialization;

import model.Node;
import model.NodeFactory;
import utils.RandomGenerator;

public class RampedGenerator extends PopulationGenerator {

	// ramped: variable depth, or better, variable maximum depth [minDepth, maxDepth]
	// - grow method (pGrow): individuals' growth may stop before the given maximum depth
	// - full method (pFull): all individuals will have depth == maximum depth
	private int minDepth;
	private int offset;		// maxDepth = minDepth + offset
	private double pFull;	// pGrow = 1 - pFull
	
	private RandomGenerator rnd = RandomGenerator.getInstance();
	
	public RampedGenerator(NodeFactory nodeFactory, int minDepth, int maxDepth, double pFull) {
		super(nodeFactory);
		
		if(minDepth>maxDepth || minDepth<0)
			throw new IllegalArgumentException("0 <= minDepth <= maxDepth");
		
		this.minDepth = minDepth;
		offset = maxDepth - minDepth;
		this.pFull = pFull;
	}

	public RampedGenerator(NodeFactory nodeFactory, int maxDepth, double pFull) {
		this(nodeFactory, maxDepth, maxDepth, pFull);
	}
	
	@Override
	public Node[] generate(int size) {
		Node[] population = new Node[size];
		
		for(int i=0; i<size; i++) {
			// offset+1 because the upper bound is exclusive
			int depthLimit = offset==0 ? minDepth : minDepth + rnd.nextInt(offset+1);
			population[i] = newIndividual(depthLimit);
		}
		
		return population;
	}

	private Node newIndividual(int depthLimit) {
		if(depthLimit == 0)
			return getFactory().getRandomTerminal();
		else {
			// full or grow method
			Node node = rnd.nextDouble() < pFull ? getFactory().getRandomFunction() : getFactory().getRandomNode();
			
			int nChildren = node.getArity();
			if(nChildren > 0) {
				Node[] children = new Node[nChildren];
				for(int i=0; i<nChildren; i++)
					children[i] = newIndividual(depthLimit-1); // recursive
				node.setChildren(children);
			}
			
			return node;
		}
	}
	
}
