package initialization;

import model.NodeFactory;

public class GrowGenerator extends RampedGenerator {

	public GrowGenerator(NodeFactory nodeFactory, int minDepth, int maxDepth) {
		super(nodeFactory, minDepth, maxDepth, 0);
	}
	
	public GrowGenerator(NodeFactory nodeFactory, int maxDepth) {
		this(nodeFactory, maxDepth, maxDepth);
	}
	
}
