package selection;

import fitness.FitnessFunction;
import model.Node;
import utils.RandomGenerator;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/23Selection.html
 * 
 * Note that tournament selection does not need to know how much better the chosen individual is compared to the others.
 * This effectively automatically re-scales fitness, so that the selection pressure on the population remains constant.
 * Conversely, it amplifies small differences in fitness to prefer the better program even if it is only marginally superior.
 */

public class TournamentSelection extends SelectionMechanism {

	private int size;
	
	public TournamentSelection(FitnessFunction fitnessFct, int size) {
		super(fitnessFct);
		this.size = size;
	}

	@Override
	public Node selectOne(Node[] population) {
		double maxFitness = Double.MIN_VALUE;
		Node bestNode = null;
		
		for(int i=0; i<size; i++) {
			Node n = population[RandomGenerator.getInstance().nextInt(population.length)];
			double fitness = getFitnessFunction().evalFitness(n);
			
			if(fitness > maxFitness) {
				maxFitness = fitness;
				bestNode = n;
			}
		}
		
		return bestNode;	
	}

}
