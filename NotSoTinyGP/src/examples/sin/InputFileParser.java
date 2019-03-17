package examples.sin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B2InputDataFilesforTinyGP.html
 * 
 * The header has the following entries
 * 		NVAR NRAND MINRAND MAXRAND NFITCASES
 * where
 * 		NVAR is an integer representing the number of variables the system should use
 * 		NRAND is an integer representing the number of random constants to be provided in the primitive set
 * 		MINRAND is a float representing the lower limit of the range used to generate random constants
 * 		MAXRAND is the corresponding upper limit
 * 		NFITCASES is an integer representing the number of fitness cases
 * NRAND can be set to 0, in which case MINRAND and MAXRAND are ignored.
 * 
 * Each fitness case is of the form
 * 		X1  . . .  XN TARGET
 * where
 * 		X1 to XN represent a set of input values for a program
 * 		TARGET represents the desired output for the given inputs.
 */

public class InputFileParser {

	private int nvar;
	private int nrand;
	private double minrand;
	private double maxrand;
	private double[][] fitnessCases;
	
	public InputFileParser(String fileName) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			StringTokenizer lineTknzr = new StringTokenizer(br.readLine()); // header line
			nvar = Integer.parseInt(lineTknzr.nextToken());
			nrand = Integer.parseInt(lineTknzr.nextToken());
			minrand = Double.parseDouble(lineTknzr.nextToken());
			maxrand = Double.parseDouble(lineTknzr.nextToken());
			
			int nfitcases = Integer.parseInt(lineTknzr.nextToken());
			fitnessCases = new double[nfitcases][];
			
			String line;
			int caseI=0;
			while((line=br.readLine()) != null) {
				double[] fcase = new double[nvar+1]; // +1 for TARGET
				
				lineTknzr = new StringTokenizer(line);
				for(int i=0; i<nvar+1; i++)
					fcase[i] = Double.parseDouble(lineTknzr.nextToken());
				
				fitnessCases[caseI++] = fcase;
			}
		}
	}

	public int getNvar() {
		return nvar;
	}

	public int getNrand() {
		return nrand;
	}

	public double getMinrand() {
		return minrand;
	}

	public double getMaxrand() {
		return maxrand;
	}

	public double[][] getFitnessCases() {
		return fitnessCases;
	}

}
