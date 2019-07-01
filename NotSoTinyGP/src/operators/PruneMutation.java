package operators;

import java.util.Random;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;
import visitor.NodeVisitor;

public class PruneMutation implements Mutation {

	private Random random;
	private int minDepth;
	private double pPrune;
	private NodeFactory factory;
	
	public PruneMutation(Random random, int minDepth, double pPrune, NodeFactory factory) {
		this.random = random;
		this.minDepth = minDepth;
		this.pPrune = pPrune;
		this.factory = factory;
	}
	
	@Override
	public Node mutate(Node n) {			
		Node node = n.clone();
		
		PruneVisitor pv = new PruneVisitor();
		node.accept(pv);
		
		return node;
	}

	private class PruneVisitor implements NodeVisitor {

		private boolean pruned;
		private int depth;
		
		public PruneVisitor() {
			depth = 0;
			pruned = false;
		}
		
		@Override
		public void visit(FunctionNode node) {
			if(pruned)
				return;
			
			if(depth >= minDepth && random.nextDouble()<pPrune) {
				Node parent = node.getParent();
				if(parent != null) {
					Node[] children = new Node[parent.getArity()];
					for(int i=0; i<parent.getArity(); i++)
						children[i] = factory.getRandomTerminal();
					parent.setChildren(children);
					pruned = true;
					
					return;
				}
			}
			
			depth++;
			for(Node child : node.getChildren())
				child.accept(this);
			depth--;
		}

		@Override
		public void visit(TerminalNode node) {
			// do nothing
		}
		
	}
	
}
