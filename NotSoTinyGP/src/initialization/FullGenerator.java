package initialization;

import model.NodeFactory;

public class FullGenerator extends RampedGenerator {

	public FullGenerator(NodeFactory nodeFactory, int minDepth, int maxDepth) {
		super(nodeFactory, minDepth, maxDepth, 1);
	}
	
	public FullGenerator(NodeFactory nodeFactory, int depth) {
		this(nodeFactory, depth, depth);
	}
	
}
