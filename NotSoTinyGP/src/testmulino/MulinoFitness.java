package testmulino;

import java.util.Random;

import model.Node;
import mulino.Checker;
import selection.FitnessFunction;

public class MulinoFitness implements FitnessFunction {

	private int minMatches;
	private int maxMoves;
	
	private PopulationScores[] popScores;

	private boolean isWhite;
	
	private Random rand;

	public MulinoFitness(Node[] popW, Node[] popB, int nMatches, int maxMoves, Random rand) {
		reStart(popW, popB);
		minMatches = nMatches;
		this.maxMoves = maxMoves;
		this.rand = rand;
		isWhite = true;
	}

	public void reStart(Node[] popW, Node[] popB) {
		popScores = new PopulationScores[] { new PopulationScores(popW), new PopulationScores(popB) };
	}
	
	public void setWhite() {
		isWhite = true;
	}

	public void setBlack() {
		isWhite = false;
	}

	public boolean isWhite() {
		return isWhite;
	}

	@Override
	public double evalFitness(Node node) {
		PopulationScores ps = popScores[isWhite ? 0 : 1];

		int i = ps.getIndex(node);
		if (ps.getMatches(i) >= minMatches)
			return ps.getScore(i) / ps.getMatches(i);
		else
			return evaluateFitness(i);
	}

	private double evaluateFitness(int i) {
		PopulationScores ps = popScores[isWhite ? 0 : 1];
		
		while(ps.getMatches(i) < minMatches) {
			// randomly select opponent from the other pop
			PopulationScores otherPop = popScores[isWhite ? 1 : 0];
			int o = otherPop.getRandom(rand);
			
			MulinoMatch match = isWhite ?
					new MulinoMatch(ps.get(i), otherPop.get(o), maxMoves, rand) :
					new MulinoMatch(otherPop.get(o), ps.get(i), maxMoves, rand);
			
			ps.addMatch(i);
			ps.addScore(i, match.getScore(isWhite ? Checker.WHITE : Checker.BLACK));
			otherPop.addMatch(o);
			otherPop.addScore(o, match.getScore(isWhite ? Checker.BLACK : Checker.WHITE));
		}
		
		return ps.getScore(i) / ps.getMatches(i);
	}

	@Override
	public double maxFitness() {
		// for termination: if (evalFitness(n) >= maxFitness()-F_DELTA)
		return Double.MAX_VALUE;
	}

	public class PopulationScores {

		private Node[] pop;
		private int[] matches;
		private double[] scores;

		public PopulationScores(Node[] pop) {
			this.pop = pop;
			matches = new int[pop.length];
			scores = new double[pop.length];

			for (int i = 0; i < pop.length; i++) {
				matches[i] = 0;
				scores[i] = 0;
			}
		}

		public int getIndex(Node n) {
			for (int i = 0; i < pop.length; i++)
				if (pop[i].equals(n))
					return i;
			return -1;
		}
		
		public Node get(int i) {
			return pop[i];
		}

		public int getMatches(int i) {
			return matches[i];
		}

		public double getScore(int i) {
			return scores[i];
		}

		public void addMatch(int i) {
			matches[i] += 1;
		}

		public void addScore(int i, double score) {
			scores[i] += score;
		}
		
		public int getRandom(Random r) {
			return r.nextInt(pop.length);
		}
	}

}
