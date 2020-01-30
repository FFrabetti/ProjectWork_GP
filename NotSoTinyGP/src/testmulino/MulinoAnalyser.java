package testmulino;

import model.Node;
import selection.FitnessFunction;
import utils.PopulationAnalyser;

public class MulinoAnalyser extends PopulationAnalyser {

	private int mindepth[];
	private double avgdepth[];
	private int frequency[];
	
	public MulinoAnalyser(Node[] pop, FitnessFunction fitnessFct) {
		super(pop, fitnessFct);
		
		// for the best node, calculate for each variable
		mindepth = new int[32];
		avgdepth = new double[32];
		frequency = new int[32];
		calculateVariableStats(getBestNode());
	}

	private void calculateVariableStats(Node bestNode) {
		for(int i=0; i<mindepth.length; i++) {
			MulinoVarVisitor mv = new MulinoVarVisitor("x"+i);
			bestNode.accept(mv);
		
			mindepth[i] = mv.getMinDepth();
			avgdepth[i] = mv.getAvgDepth();
			frequency[i] = mv.getCount();
		}
	}

	public int[] getMindepth() {
		return mindepth;
	}

	public double[] getAvgdepth() {
		return avgdepth;
	}

	public int[] getFrequency() {
		return frequency;
	}

}
