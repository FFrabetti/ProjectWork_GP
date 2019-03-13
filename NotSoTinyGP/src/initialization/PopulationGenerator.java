package initialization;

import model.Node;
import model.NodeFactory;

public abstract class PopulationGenerator {

	private NodeFactory nodeFactory;
	
	public PopulationGenerator(NodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
	}
	
	// to be used by its subclasses
	protected NodeFactory getFactory() {
		return nodeFactory;
	}
	
	public abstract Node[] generate(int size);
	
}
