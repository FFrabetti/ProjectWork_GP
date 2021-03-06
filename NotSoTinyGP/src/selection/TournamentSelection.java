package selection;

import java.util.Random;

import model.Node;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/23Selection.html
 * 
 * Note that tournament selection does not need to know how much better the chosen individual is compared to the others.
 * This effectively automatically re-scales fitness, so that the selection pressure on the population remains constant.
 * Conversely, it amplifies small differences in fitness to prefer the better program even if it is only marginally superior.
 */

public class TournamentSelection extends SelectionMechanism {

	private Random random;
	private int size; // tournament size
	
	public TournamentSelection(Random random, FitnessFunction fitnessFct, int size) {
		super(fitnessFct);
		this.random = random;
		this.size = size;
	}

	@Override
	public Node selectOne(Node[] population) {
		double maxFitness = -Double.MAX_VALUE;
		Node best = null;
		
		for(int i=0; i<size; i++) {
			Node n = population[random.nextInt(population.length)];
			// TODO: just one evaluation for each individual -> store fitness for future uses
			double fitness = getFitnessFunction().evalFitness(n);
			
			if(fitness > maxFitness) {
				maxFitness = fitness;
				best = n;
			}
		}
		
		return best;	
	}

}
