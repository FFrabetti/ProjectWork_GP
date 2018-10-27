package operators;

import model.Node;

public interface Mutation {

	// returns a NEW node object, without altering n
	public Node mutate(Node n);
	
}
