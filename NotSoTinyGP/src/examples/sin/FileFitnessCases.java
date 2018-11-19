package examples.sin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import fitness.FitnessFunction;
import model.Node;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B2InputDataFilesforTinyGP.html
 * 
 * The header has the following entries
 * 		NVAR NRAND MINRAND MAXRAND NFITCASES
 * where
 *		NVAR is an integer representing the number of variables the system should use
 *		...
 * 		NFITCASES is an integer representing the number of fitness cases
 * 
 * Each fitness case is of the form
 * 		X1  . . .  XN TARGET
 * where
 * 		X1 to XN represent a set of input values for a program
 * 		TARGET represents the desired output for the given inputs.
 */

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B1OverviewofTinyGP.html
 * 
 * 11.	The fitness function is minus the sum of the absolute differences between 
 * 		the actual program output and the desired output for each fitness case.
 */

public class FileFitnessCases implements FitnessFunction {

	private double[][] fitnessCases;
	
	public FileFitnessCases(String fileName) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			StringTokenizer lineTknzr = new StringTokenizer(br.readLine()); // header line
			int nvar = Integer.parseInt(lineTknzr.nextToken());
			lineTknzr.nextToken(); // nrand
			lineTknzr.nextToken(); // minrand
			lineTknzr.nextToken(); // maxrand
			
			int nfitcases = Integer.parseInt(lineTknzr.nextToken());
			fitnessCases = new double[nfitcases][];
			
			String line;
			int caseI=0;
			while((line=br.readLine()) != null) {
				double[] fcase = new double[nvar+1]; // +1 for the desired result
				
				lineTknzr = new StringTokenizer(line);
				for(int i=0; i<nvar+1; i++)
					fcase[i] = Double.parseDouble(lineTknzr.nextToken());
				
				fitnessCases[caseI++] = fcase;
			}
		}
	}
	
	@Override
	public double evalFitness(Node n) {
		double sum = 0;
		
		for(double[] fcase : fitnessCases) {
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
	
}
