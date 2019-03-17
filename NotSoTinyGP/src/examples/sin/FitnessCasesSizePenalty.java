package examples.sin;

import model.Node;
import visitor.CountVisitor;

public class FitnessCasesSizePenalty extends FitnessCases {

	private int nCases;
	
	public FitnessCasesSizePenalty(double[][] fitnessCases) {
		super(fitnessCases);
		nCases = fitnessCases.length;
	}
	
	@Override
	public double evalFitness(Node n) {
		double sum = super.evalFitness(n);
		
		CountVisitor cv = new CountVisitor();
		n.accept(cv);
		int s = cv.getSize();
		int d = cv.getDepth();

		return sum - d/(nCases*2.0); // if d2-d1 == 1, f1-f2 == 1/(2*nCases)
		
//		int x = d+1; // [1,+inf[
//		double alpha = 1;
		
		// always p != 0
//		double p = Math.log(x+0.1);
//		double p = x;
//		double p = x*x;
		
//		return sum * alpha * p;
	}

}
