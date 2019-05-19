package initialization;

import model.Node;
import model.NodeFactory;

public class FullGenerator extends RampedGenerator {

//	public FullGenerator(Random random, NodeFactory nodeFactory, int minDepth, int maxDepth) {
//		super(random, nodeFactory, minDepth, maxDepth, 1);
//	}
	
	public FullGenerator(NodeFactory nodeFactory, int depth) {
		super(null, nodeFactory, depth, depth, 1);
	}
	
	@Override
	// all nodes but the ones at depth=<depth> are functions
	protected Node generateNode() {
		return getFactory().getRandomFunction();
	}
	
}
