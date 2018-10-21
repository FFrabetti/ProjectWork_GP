package initialization;

import model.NodeFactory;

public class GrowGenerator extends RampedHalfAndHalfGenerator {

	public GrowGenerator(NodeFactory nodeFactory, int maxDepth) {
		super(nodeFactory, maxDepth, maxDepth, 0);
	}
	
}
