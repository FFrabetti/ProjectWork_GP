package examples.sin;

import java.util.HashMap;
import java.util.Map;

import model.Node;
import selection.FitnessFunction;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B1OverviewofTinyGP.html
 * 
 * 11.	The fitness function is minus the sum of the absolute differences between 
 * 		the actual program output and the desired output for each fitness case.
 */

public class FileFitnessCases implements FitnessFunction {

	private double[][] fitnessCases;
	
	public FileFitnessCases(double[][] fitnessCases) {
		this.fitnessCases = fitnessCases;
	}
	
	@Override
	public double evalFitness(Node n) {
		double sum = 0;
		
		for(double[] fcase : fitnessCases) {
			// for each variable load its input value
			Map<String,Double> env = new HashMap<>();
			for(int i=0; i<fcase.length-1; i++)
				env.put("x"+i, fcase[i]);
			
			sum -= Math.abs(fcase[fcase.length-1]-eval(n, env));
		}
		
		return sum;
	}

	private double eval(Node n, Map<String, Double> env) {
		EvalVisitor ev = new EvalVisitor(env);
		n.accept(ev);
		return ev.getResult();
	}

	@Override
	public double maxFitness() {
		return 0;
	}
	
}
