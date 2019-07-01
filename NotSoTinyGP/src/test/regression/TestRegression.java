package test.regression;

import static utils.Launcher.getDouble;
import static utils.Launcher.getInt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import evolution.TimeMachine;
import examples.regression.DoubleOpNodeFactoryEpsilon;
import examples.regression.FitnessCases;
import examples.regression.InputFileParser;
import initialization.PopulationGenerator;
import initialization.RampedHalfAndHalfGenerator;
import model.Node;
import model.NodeFactory;
import operators.BaseOperator;
import operators.Crossover;
import operators.Mutation;
import operators.Operator;
import operators.PointMutation;
import operators.SubtreeCrossover;
import selection.FitnessFunction;
import selection.SelectionMechanism;
import selection.TournamentSelection;
import utils.DataExporter;
import utils.Launchable;
import utils.Launcher;
import utils.PopulationAnalyser;
import visitor.CountVisitor;

public class TestRegression implements Launchable {

	// using the same parameters of TinyGP
	private static final int POPSIZE = 100000;
	private static final int DEPTH = 5;
	private static final double CROSSOVER_PROB = 0.9;
	private static final double PMUT_PER_NODE = 0.05;
	private static final int GENERATIONS = 100;
	private static final int TSIZE = 2;

	// new:
	private static final double TERMINAL_PROB = 0.5;
	private static final double MUTATION_PROB = 0.08;
	// reproduction_prob = 1 - CROSSOVER_PROB - MUTATION_PROB
	private static final double FITNESS_DELTA = 0.05; // ok if fitness >= maxFitness-DELTA
	private static final String FILE_NAME = "resources/polynomial-data.txt";
	
	// args: resources/polynomial-data.txt 2 polynomial.properties
	public static void main(String[] args) {
		Launcher.launch(args, new TestRegression());
	}

	private static Node bestIndividual(Node[] pop, FitnessFunction fitness) {
		return Stream.of(pop).max((n1, n2) -> (int) Math.signum(fitness.evalFitness(n1) - fitness.evalFitness(n2)))
				.get(); // Stream.max() returns an Optional
	}

	@Override
	public void launch(Properties properties) {
		Random random = (Random) properties.get("random");

		double pTerm = getDouble(properties, "pTerm", TERMINAL_PROB);
		
		String fileName = properties.getProperty("configFile", FILE_NAME);
		InputFileParser parser;
		try {
			parser = new InputFileParser(fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		NodeFactory factory = new DoubleOpNodeFactoryEpsilon(random,
				parser.getNvar(), parser.getNrand(),
				parser.getMinrand(), parser.getMaxrand(),
				pTerm);

		int mindepth = getInt(properties, "mindepth", DEPTH);
		int maxdepth = getInt(properties, "maxdepth", DEPTH);
		PopulationGenerator generator = new RampedHalfAndHalfGenerator(random, factory, mindepth, maxdepth);

		FitnessFunction fitness = new FitnessCases(parser.getFitnessCases());

		int tsize = getInt(properties, "tsize", TSIZE);
		SelectionMechanism selection = new TournamentSelection(random, fitness, tsize);

		// operators
		double pCrossover = getDouble(properties, "crossover_prob", CROSSOVER_PROB);
		Crossover stxo = new SubtreeCrossover(random);
		Operator crossover = new BaseOperator(pCrossover,
				pop -> stxo.apply(selection.selectOne(pop), selection.selectOne(pop)));

		double pMutNode = getDouble(properties, "pmut_per_node", PMUT_PER_NODE);
		double pMutation = getDouble(properties, "mutation_prob", MUTATION_PROB);
		Mutation ptm = new PointMutation(random, factory, pMutNode);
		Operator mutation = new BaseOperator(pMutation, pop -> ptm.mutate(selection.selectOne(pop)));

		double pReproduction = 1 - pCrossover - pMutation;
		Operator reproduction = new BaseOperator(pReproduction, pop -> selection.selectOne(pop).clone());

		// time machine
		int popsize = getInt(properties, "popsize", POPSIZE);
		Node[] initialPop = generator.generate(popsize);
		TimeMachine tm = new TimeMachine(random, new Operator[] { crossover, mutation, reproduction });

		// termination: fitness of the best individual above a certain threshold
		double fitnessDelta = getDouble(properties, "fitness_delta", FITNESS_DELTA);
		Predicate<Node[]> terminationCriterion = pop -> fitness
				.evalFitness(bestIndividual(pop, fitness)) >= fitness.maxFitness() - fitnessDelta;
		
		List<PopulationAnalyser> listPA = new LinkedList<>();
				
		System.out.println("TimeMachine.run()");
		Consumer<Node[]> action = pop -> {
			PopulationAnalyser pa = new PopulationAnalyser(pop, fitness);
			pa.printStats();
			pa.printFitness();
			System.out.println("------------------------------------");
			
			// for each generation, save min/max/av. depth, size, fitness
			listPA.add(pa);
		};
		action.accept(initialPop); // generation 0		
		int generations = getInt(properties, "generations", GENERATIONS);
		int gen = tm.run(initialPop, generations, terminationCriterion, action);

		System.out.println("\nRESULT");
		System.out.println("generation = " + gen + ", isSuccess = " + tm.isSuccess(terminationCriterion));
		Node best = bestIndividual(tm.getCurrentGeneration(), fitness);
		System.out.println("best fitness = " + fitness.evalFitness(best));
		System.out.println("best node = " + best);
		
		// write run information on file
		System.out.println("\n");
		long timestamp = System.currentTimeMillis();
		String path = "runs/" + TestRegression.class.getSimpleName() + "_" + timestamp;
		createCSV(initialPop, path + "_initial", fitness);
		createCSV(tm.getCurrentGeneration(), path + "_final", fitness);
		createPropertiesFile(properties, path); // save snapshot for future reference
		createResultFile(gen, tm.isSuccess(terminationCriterion), best, fitness.evalFitness(best), path);
		createSummaryCSV(listPA, path);
	}

	// https://wiki.openoffice.org/wiki/Documentation/How_Tos/Calc:_FREQUENCY_function
	// http://wikieducator.org/OpenOffice/Calc_3/Histogram
	private void createCSV(Node[] pop, String name, FitnessFunction fitness) {
		Function<Node, String[]> funct = n -> { // for each node
			CountVisitor v = new CountVisitor();
			n.accept(v);
			
			return new String[] {
					String.valueOf(v.getDepth()),
					String.valueOf(v.getSize()),
					String.valueOf(fitness.evalFitness(n))};
		};
		String[] labels = new String[] {"depth","size","fitness"};
		
		DataExporter.createCSV(pop, name + ".csv", funct, labels);
		// REMEMBER to set an "English locale" when you open them (decimal separator . )
	}

	private void createPropertiesFile(Properties properties, String name) {
		properties.remove("random"); // not serializable
		
		try(PrintWriter pw = new PrintWriter(new FileWriter(name + ".properties"))) {
			properties.forEach((k, v) -> {
				if(v instanceof Serializable)
					pw.println(k + "=" + v.toString());
			});
			
			System.out.println("Created file: " + name + ".properties");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		try {
//			properties.store(new FileWriter(timestamp + ".properties"), "test comment");
//			
//			System.out.println("Created file: " + timestamp + ".properties");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private void createResultFile(int gen, boolean success, Node best, double fitness, String name) {
		try(PrintWriter pw = new PrintWriter(new FileWriter(name + ".result"))) {
			pw.println("generation = " + gen + " - " + (success ? "SUCCESS" : "FAIL"));
			pw.println("best fitness = " + fitness);
			pw.println("best node = " + best);
			
			System.out.println("Created file: " + name + ".result");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createSummaryCSV(List<PopulationAnalyser> listPA, String path) {
		String[][] lines = new String[listPA.size()][];
		int i = 0;
		for(PopulationAnalyser pa : listPA) {
			lines[i] = new String[] {String.valueOf(i), // generation
					String.valueOf(pa.getMinDepth()), String.valueOf(pa.getMaxDepth()), String.valueOf(pa.getAvDepth()),
					String.valueOf(pa.getMinSize()), String.valueOf(pa.getMaxSize()), String.valueOf(pa.getAvSize()),
					String.valueOf(pa.getMaxFitness()), String.valueOf(pa.getAvFitness())};
			i++;
		}
		
		DataExporter.createCSV(lines, path + "_summary.csv", new String[] {"generation",
				"minDepth", "maxDepth", "avDepth", "minSize", "maxSize", "avSize", "maxFitness", "avFitness"});
	}

}
