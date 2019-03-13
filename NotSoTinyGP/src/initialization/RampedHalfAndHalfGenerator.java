package initialization;

import model.NodeFactory;

public class RampedHalfAndHalfGenerator extends RampedGenerator {

	public RampedHalfAndHalfGenerator(NodeFactory nodeFactory, int minDepth, int maxDepth) {
		super(nodeFactory, minDepth, maxDepth, 0.5); // half-and-half
	}

	public RampedHalfAndHalfGenerator(NodeFactory nodeFactory, int maxDepth) {
		this(nodeFactory, maxDepth, maxDepth);
	}
	
}
