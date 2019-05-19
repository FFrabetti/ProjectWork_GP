package initialization;

import model.Node;
import model.NodeFactory;

public class GrowGenerator extends RampedGenerator {

//	public GrowGenerator(Random random, NodeFactory nodeFactory, int minDepth, int maxDepth) {
//		super(random, nodeFactory, minDepth, maxDepth, 0);
//	}
	
	public GrowGenerator(NodeFactory nodeFactory, int maxDepth) {
		super(null, nodeFactory, maxDepth, maxDepth, 0);
	}
	
	@Override
	protected Node generateNode() {
		return getFactory().getRandomNode();
	}
	
}
