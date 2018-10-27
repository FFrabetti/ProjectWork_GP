package operators;

import initialization.PopulationGenerator;
import model.Node;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/24RecombinationandMutation.html
 * 
 * S.M. randomly selects a mutation point in a tree and substitutes the subtree rooted there with a randomly generated subtree.
 * It can be implemented as a crossover between a program and a newly generated random program ("headless chicken" crossover). 
 */

public class SubtreeMutation implements Mutation {

	private PopulationGenerator generator;
	private Crossover crossover;
	
	public SubtreeMutation(PopulationGenerator generator, Crossover crossover) {
		this.generator = generator;
		this.crossover = crossover;
	}

	@Override
	public Node mutate(Node n) {
		return crossover.apply(n, generator.generate(1)[0]);
	}

}
