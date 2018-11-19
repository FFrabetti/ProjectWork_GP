package test.sin;

import java.io.IOException;
import java.util.stream.DoubleStream;

import examples.sin.FileFitnessCases;
import examples.sin.SinFactory;
import fitness.FitnessFunction;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import model.Node;
import model.NodeFactory;
import utils.RandomGenerator;

public class TestFactoryAndFitness {
	
	public static void main(String[] args) throws IOException {
		String fileName = "resources/sin/double-toy-data.txt"; // "sin-data.txt";
		int maxDepth = 2;
		int popSize = 3; // 10
		
		RandomGenerator.getInstance().setSeed(2);
		
		NodeFactory factory = new SinFactory(fileName);
		FitnessFunction fitnessFct = new FileFitnessCases(fileName);
		
		PopulationGenerator generator = new GrowGenerator(factory, maxDepth);
		Node[] population = generator.generate(popSize);
		
		double[] fitness = new double[popSize];
		for(int i=0; i<popSize; i++) {
			fitness[i] = fitnessFct.evalFitness(population[i]);
			
			System.out.println(population[i] + "\t\t\t\t" + fitness[i]);
		}
		
		System.out.println("\nMAX fitness = " + DoubleStream.of(fitness).max().getAsDouble());
	}

}
