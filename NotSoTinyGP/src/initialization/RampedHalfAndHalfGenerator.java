package initialization;

import java.util.Random;

import model.NodeFactory;

public class RampedHalfAndHalfGenerator extends RampedGenerator {

	public RampedHalfAndHalfGenerator(Random random, NodeFactory nodeFactory, int minDepth, int maxDepth) {
		super(random, nodeFactory, minDepth, maxDepth, 0.5); // half-and-half
	}

//	public RampedHalfAndHalfGenerator(NodeFactory nodeFactory, int maxDepth) {
//		this(nodeFactory, maxDepth, maxDepth);
//	}
	
}
