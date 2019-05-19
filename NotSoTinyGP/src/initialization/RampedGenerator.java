package initialization;

import java.util.Random;

import model.Node;
import model.NodeFactory;

public class RampedGenerator extends PopulationGenerator {

	// ramped: variable depth, or better, variable maximum depth [minDepth, maxDepth]
	// - grow method (pGrow): individuals' growth may stop before the given maximum depth
	// - full method (pFull): all individuals will have depth == maximum depth
	private int minDepth;
	private int offset;		// maxDepth = minDepth + offset
	private double pFull;	// pGrow = 1 - pFull
	
	private Random random;
	
	public RampedGenerator(Random random, NodeFactory nodeFactory, int minDepth, int maxDepth, double pFull) {
		super(nodeFactory);
		
		if(minDepth>maxDepth || minDepth<0)
			throw new IllegalArgumentException("0 <= minDepth <= maxDepth");
		
		this.random = random;
		this.minDepth = minDepth;
		offset = maxDepth - minDepth;
		this.pFull = pFull;
	}

	// ramped means of variable depth
//	public RampedGenerator(Random random, NodeFactory nodeFactory, int maxDepth, double pFull) {
//		this(random, nodeFactory, maxDepth, maxDepth, pFull);
//	}
	
	@Override
	public Node[] generate(int size) {
		Node[] population = new Node[size];
		for(int i=0; i<size; i++) 
			population[i] = newIndividual(getDepthLimit());
		return population;
	}

	private Node newIndividual(int depthLimit) {
		if(depthLimit == 0)
			return getFactory().getRandomTerminal();
		else {
			Node node = generateNode();
			
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
	
	// sub-classes can override them:
	// these are the parts of the pop. generation that are more likely to be changed
	
	protected int getDepthLimit() {
		// offset+1 because the upper bound is exclusive
		return offset==0 ? minDepth : minDepth + random.nextInt(offset+1);
	}
	
	protected Node generateNode() {
		// full or grow method
		return random.nextDouble() < pFull ? getFactory().getRandomFunction() : getFactory().getRandomNode();
	}
	
}
