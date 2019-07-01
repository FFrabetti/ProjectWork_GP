package utils;

import model.Node;
import selection.FitnessFunction;
import visitor.CountVisitor;

public class PopulationAnalyser {

	// legacy static methods
	public static PopulationAnalyser printStats(Node[] pop) {
		PopulationAnalyser pa = new PopulationAnalyser(pop);
		pa.printStats();
		return pa;
	}
	
	public static PopulationAnalyser printFitness(Node[] pop, FitnessFunction fitnessFct) {
		PopulationAnalyser pa = new PopulationAnalyser(pop, fitnessFct);
		pa.printFitness();
		return pa;
	}

	// depth and size of a population
	private void calculateStats(Node[] pop) {
		int totDepth = 0;
		int totSize = 0;

		int maxDepth = -1;
		int minDepth = Integer.MAX_VALUE;
		int maxSize = -1;
		int minSize = Integer.MAX_VALUE;

		for (Node n : pop) {
			CountVisitor v = new CountVisitor();
			n.accept(v);
			int depth = v.getDepth();
			int size = v.getSize();

			totDepth += depth;
			totSize += size;

			if (depth > maxDepth)
				maxDepth = depth;
			if (depth < minDepth)
				minDepth = depth;

			if (size > maxSize)
				maxSize = size;
			if (size < minSize)
				minSize = size;
		}

		avDepth = totDepth / (float) pop.length;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
		avSize = totSize / (float) pop.length;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	private void calculateFitness(Node[] pop, FitnessFunction fitnessFct) {
		double maxFitness = -Double.MAX_VALUE;
		Node bestIndividual = null;
		double totFitness = 0;
		
		for (Node n : pop) {
			double fitness = fitnessFct.evalFitness(n);
			totFitness += fitness;
			
			if (fitness > maxFitness) {
				maxFitness = fitness;
				bestIndividual = n;
			}
		}

		avFitness = totFitness/pop.length;
		this.maxFitness = maxFitness;
		bestNode = bestIndividual;
	}
	
	// --------------------------------------------------------------------------------
	
	private int minDepth, maxDepth;
	private int minSize, maxSize;
	private double avDepth, avSize;
	private double maxFitness, avFitness;
	private Node bestNode;
	
	public PopulationAnalyser(Node[] pop, FitnessFunction fitnessFct) {
		calculateStats(pop);
		calculateFitness(pop, fitnessFct);
	}

	public PopulationAnalyser(Node[] pop) {
		calculateStats(pop);
	}
	
	public void printStats() {
		System.out.println("Av. depth = " + avDepth);
		System.out.println("Min depth = " + minDepth + ", Max depth = " + maxDepth);
		System.out.println("Av. size = " + avSize);
		System.out.println("Min size = " + minSize + ", Max size = " + maxSize);
	}

	public void printFitness() {
		System.out.println("Av. fitness = " + avFitness);
		System.out.println("Max fitness = " + maxFitness);
		System.out.println("Best ind. = " + bestNode);
	}

	public int getMinDepth() {
		return minDepth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public int getMinSize() {
		return minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public double getAvDepth() {
		return avDepth;
	}

	public double getAvSize() {
		return avSize;
	}

	public Node getBestNode() {
		return bestNode;
	}

	public double getMaxFitness() {
		return maxFitness;
	}

	public double getAvFitness() {
		return avFitness;
	}
	
}
