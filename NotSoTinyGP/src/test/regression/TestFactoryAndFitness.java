package test.regression;

import java.io.IOException;
import java.util.Random;
import java.util.stream.DoubleStream;

import examples.regression.DoubleNode;
import examples.regression.DoubleOpNodeFactoryEpsilon;
import examples.regression.FitnessCases;
import examples.regression.InputFileParser;
import examples.regression.TimesNode;
import examples.regression.VarNode;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import model.Node;
import model.NodeFactory;
import selection.FitnessFunction;

public class TestFactoryAndFitness {
	
	public static void main(String[] args) throws IOException {
		Random random = new Random(2);
		
		String fileName = "resources/double-toy-data.txt";
		int maxDepth = 2;
		int popSize = 10;
		
		InputFileParser parser = new InputFileParser(fileName);		
		NodeFactory factory = new DoubleOpNodeFactoryEpsilon(random,
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
			System.out.println("\tfitness = " + DoubleNode.formatter.format(fitness[i]) + " (" + fitness[i] + ")");
		}
		
		System.out.println("\nMAX fitness = " + DoubleStream.of(fitness).max().getAsDouble());
		
		Node n = new TimesNode(new DoubleNode(2), new VarNode("x0"));
		System.out.println("----\nn = " + n);
		System.out.println("\tfitness = " + fitnessFct.evalFitness(n));
	}

}
