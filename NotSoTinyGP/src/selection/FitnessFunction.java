package selection;

import model.Node;

public interface FitnessFunction {

	double evalFitness(Node node);

	// for termination: if (evalFitness(n) >= maxFitness()-F_DELTA)
	double maxFitness();
	
}
