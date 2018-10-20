package initialpop;

import model.NodeFactory;

public class FullGenerator extends RampedHalfAndHalfGenerator {

	public FullGenerator(NodeFactory nodeFactory, int maxDepth) {
		super(nodeFactory, maxDepth, maxDepth, 1);
	}
	
}
