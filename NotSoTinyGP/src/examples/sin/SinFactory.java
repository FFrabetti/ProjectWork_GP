package examples.sin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import model.FunctionNode;
import model.Node;
import model.NodeFactory;
import model.TerminalNode;
import utils.RandomGenerator;

/*
 * https://cswww.essex.ac.uk/staff/rpoli/gp-field-guide/B1OverviewofTinyGP.html
 * 
 * 1.	The terminal set includes a user-definable number of floating point variables (named X1 to XN).
 * 2.	The function set includes multiplication, protected division, subtraction and addition.
 */

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
 */

public class SinFactory extends NodeFactory {
	
	private RandomGenerator rand = RandomGenerator.getInstance();
	private int nTerminals;
	private List<TerminalNode> terminals;
	private String[] functions = new String[] {"+", "-", "*", "/"};
	
	public SinFactory(String fileName) throws IOException {
		int nvar;
		int nrand;
		double minrand;
		double maxrand;
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			StringTokenizer lineTknzr = new StringTokenizer(br.readLine()); // header line
			nvar = Integer.parseInt(lineTknzr.nextToken());
			nrand = Integer.parseInt(lineTknzr.nextToken());
			minrand = Double.parseDouble(lineTknzr.nextToken());
			maxrand = Double.parseDouble(lineTknzr.nextToken());
		}
		
		nTerminals = nvar + nrand;
		terminals = new ArrayList<>(nTerminals);
		for(int i=0; i<nvar; i++)
			terminals.add(new VarNode("x"+i));
		for(int i=0; i<nrand; i++)
			terminals.add(new NumNode(rand.nextDouble()*(maxrand-minrand)+minrand));
	}

	@Override
	public Node getRandomNode() {
		// p = |terminal_set|/(|terminal_set|+|function_set|)
//		double p = nTerminals/(float)(nTerminals+functions.length);
		// TODO: nTerminals >> functions.length --> see what TinyGP does
		
		double p = 0.5;
		return rand.nextDouble() < p ? getRandomTerminal() : getRandomFunction();
	}

	@Override
	public TerminalNode getRandomTerminal() {
		return (TerminalNode) terminals.get(rand.nextInt(nTerminals)).clone();
	}

	@Override
	public FunctionNode getRandomFunction() {
		return new OpNode(functions[rand.nextInt(functions.length)]);
	}

	@Override
	public Node getRandomNode(int arity) {
		return arity==0 ? getRandomTerminal() : getRandomFunction();
	}

}
