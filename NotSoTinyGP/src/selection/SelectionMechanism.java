package selection;

import fitness.FitnessFunction;
import model.Node;

public abstract class SelectionMechanism {

	private FitnessFunction fitnessFct;
	
	public SelectionMechanism(FitnessFunction fitnessFct) {
		this.fitnessFct = fitnessFct;
	}
	
	public FitnessFunction getFitnessFunction() {
		return fitnessFct;
	}
	
	public abstract Node selectOne(Node[] population);
	
}
