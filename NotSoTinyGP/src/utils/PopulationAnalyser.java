package utils;

import model.Node;
import selection.FitnessFunction;
import visitor.CountVisitor;

public class PopulationAnalyser {

	// depth and size of a population
	public static void printStats(Node[] pop) {
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

		System.out.println("Av. depth = " + totDepth / (float) pop.length);
		System.out.println("Min depth = " + minDepth + ", Max depth = " + maxDepth);
		System.out.println("Av. size = " + totSize / (float) pop.length);
		System.out.println("Min size = " + minSize + ", Max size = " + maxSize);
	}

	public static void printFitness(Node[] pop, FitnessFunction fitnessFct) {
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

		System.out.println("Av. fitness = " + totFitness/pop.length);
		System.out.println("Max fitness = " + maxFitness);
		System.out.println("Best ind. = " + bestIndividual);
	}
	
}
