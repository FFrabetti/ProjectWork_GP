package test.sin;

import java.io.IOException;
import java.util.stream.DoubleStream;

import examples.sin.DoubleNode;
import examples.sin.FitnessCases;
import examples.sin.InputFileParser;
import examples.sin.SinFactory;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import model.Node;
import model.NodeFactory;
import selection.FitnessFunction;
import utils.RandomGenerator;

public class TestFactoryAndFitness {
	
	public static void main(String[] args) throws IOException {
		RandomGenerator.getInstance().setSeed(2);
		
		String fileName = "resources/sin/double-toy-data.txt";
		int maxDepth = 2;
		int popSize = 3; // 10
		
		InputFileParser parser = new InputFileParser(fileName);		
		NodeFactory factory = new SinFactory(
				parser.getNvar(),
				parser.getNrand(),
				parser.getMinrand(),
				parser.getMaxrand());
		FitnessFunction fitnessFct = new FitnessCases(parser.getFitnessCases());
		
		PopulationGenerator generator = new GrowGenerator(factory, maxDepth);
		Node[] pop = generator.generate(popSize);
		
		double[] fitness = new double[popSize];
		for(int i=0; i<popSize; i++) {
			fitness[i] = fitnessFct.evalFitness(pop[i]);
			System.out.println("n" + i + " = " + pop[i]);
			System.out.println("fitness = " + DoubleNode.formatter.format(fitness[i]) + " (" + fitness[i] + ")");
		}
		
		System.out.println("\nMAX fitness = " + DoubleStream.of(fitness).max().getAsDouble());
	}

}
