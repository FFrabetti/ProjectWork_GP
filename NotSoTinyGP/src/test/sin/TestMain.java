package test.sin;
import java.io.IOException;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import evolution.TimeMachine;
import examples.sin.FitnessCases;
import examples.sin.FitnessCasesSizePenalty;
import examples.sin.InputFileParser;
import examples.sin.SinFactory;
import examples.sin.SinFactoryEps;
import initialization.FullGenerator;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
import model.Node;
import model.NodeFactory;
import operators.Crossover;
import operators.BaseOperator;
import operators.Mutation;
import operators.Operator;
import operators.PointMutation;
import operators.SubtreeCrossover;
import operators.SubtreeMutation;
import selection.FitnessFunction;
import selection.SelectionMechanism;
import selection.TournamentSelection;
import utils.DataExporter;
import utils.PopulationAnalyser;
import visitor.CountVisitor;

public class TestMain {

	// using the same parameters of TinyGP
	private static final int SEED = 1;					// <0 for no fixed seed
	private static final int POPSIZE = 500;
	private static final int DEPTH = 2;					// (max) depth of the initial pop.
	private static final double CROSSOVER_PROB = 0.9;
	private static final double PMUT_PER_NODE = 0.05;	// for PointMutation
	private static final int GENERATIONS = 100;			// max nr of generations
	private static final int TSIZE = 8;					// tournament size

	// new:
	private static final double MUTATION_PROB = 0.02;
	private static final double REPRODUCTION_PROB = 1 - CROSSOVER_PROB - MUTATION_PROB;
	private static final double FITNESS_DELTA = 0.05; // ok if fitness >= maxFitness-DELTA
	
	// other options:
	// - use sin-data.txt or 2x+1-toy-data.txt
	private static final String FILE = "sin-data.txt";
	// - use SinFactory or SinFactoryEps
	private static final boolean USE_SINFACTORY = false;
	// - use Full or Grow method
	private static final boolean USE_FULLGROWTH = false;
	// - use PointMutation or SubtreeMutation
	private static final boolean USE_POINTMUT = true;
	// - use FitnessCases or FitnessCasesSizePenalty
	private static final boolean USE_SIZEPENALTY = true;
	
	// -- decomment lines for (automatic) run() and for DataExporter.createCSV
	
	private static Random random = new Random();
	
	public static void main(String[] args) throws IOException {
		if(SEED >= 0)
			random.setSeed(SEED);
		
		String fileName = "resources/sin/" + FILE;
		InputFileParser parser = new InputFileParser(fileName);
		
		NodeFactory factory = getNodeFactory(parser);
		PopulationGenerator generator = getPopulationGenerator(factory);
		
		FitnessFunction fitness = getFitnessFunction(parser);
		SelectionMechanism selection = new TournamentSelection(random, fitness, TSIZE);

		// operators
		Crossover stxo = new SubtreeCrossover(random);
		Operator crossover = new BaseOperator(CROSSOVER_PROB,
				pop -> stxo.apply(selection.selectOne(pop), selection.selectOne(pop)));
		
		Mutation ptm = getMutation(factory, generator, stxo);
		Operator mutation = new BaseOperator(MUTATION_PROB,
				pop -> ptm.mutate(selection.selectOne(pop)));
		
		Operator reproduction = new BaseOperator(REPRODUCTION_PROB,
				pop -> selection.selectOne(pop).clone());
		
		// time machine
		Node[] initialPop = generator.generate(POPSIZE);
		TimeMachine tm = new TimeMachine(random, new Operator[] {crossover, mutation, reproduction});

		// termination: fitness of the best individual above a certain threshold
		Predicate<Node[]> terminationCriterion = pop -> 
				fitness.evalFitness(bestIndividual(pop, fitness)) >= fitness.maxFitness()-FITNESS_DELTA;
		
//		System.out.println("TimeMachine.run()");
//		Consumer<Node[]> action = pop -> {
//			PopulationAnalyser.printStats(pop);
//			PopulationAnalyser.printFitness(pop, fitness);
//			System.out.println("------------------------------------");
//		};
//		int gen = tm.run(initialPop, GENERATIONS, terminationCriterion); // , action);
//		System.out.println("generations = " + gen + ", isSuccess = " + tm.isSuccess(terminationCriterion));
//		Node best = bestIndividual(tm.getCurrentGeneration(), fitness);
//		System.out.println("best fitness = " + fitness.evalFitness(best));
//		System.out.println("best node = " + best);
		
		// "manual" execution
		System.out.println("\n\n%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		Node[] pop = initialPop;
		for(int i=0; i<=GENERATIONS && !terminationCriterion.test(pop); i++) {
			if(i!=0) // for generation 0 just do the prints
				pop = tm.nextGeneration(pop);
			
			System.out.println(i + " GENERATION");
			PopulationAnalyser.printStats(pop);
			PopulationAnalyser.printFitness(pop, fitness);
			System.out.println("------------------------------------");
		}
		if(terminationCriterion.test(pop))
			System.out.println("SUCCESS");
		else
			System.out.println("FAILURE");
		Node best = bestIndividual(pop, fitness);
		System.out.println("BEST (f=" + fitness.evalFitness(best) + ") = " + best);
		
		FitnessFunction fc = new FitnessCases(parser.getFitnessCases());
		System.out.println("error = " + -fc.evalFitness(best));
		PopulationAnalyser.printFitness(pop, fc);
		
		// https://wiki.openoffice.org/wiki/Documentation/How_Tos/Calc:_FREQUENCY_function
		// http://wikieducator.org/OpenOffice/Calc_3/Histogram
//		String prefix = TestMain.class.getName();
//		long suffix = System.currentTimeMillis();
//		String path = "runs/";
//		
//		Function<Node, String[]> funct = n -> { // for each node
//			CountVisitor v = new CountVisitor();
//			n.accept(v);
//			
//			return new String[] {
//					String.valueOf(v.getDepth()),
//					String.valueOf(v.getSize()),
//					String.valueOf(fitness.evalFitness(n))};
//		};
//		String[] labels = new String[] {"depth","size","fitness"};
//		
//		DataExporter.createCSV(initialPop, path + prefix + suffix + "_initial.csv", funct, labels);
//		DataExporter.createCSV(pop, path + prefix + suffix + "_final.csv", funct, labels);
		// REMEMBER to set an "English locale" when you open them (decimal separator . )
	}

	private static FitnessFunction getFitnessFunction(InputFileParser parser) {
		if(USE_SIZEPENALTY)
			return new FitnessCasesSizePenalty(parser.getFitnessCases());
		else
			return new FitnessCases(parser.getFitnessCases());
	}

	private static PopulationGenerator getPopulationGenerator(NodeFactory factory) {
		if(USE_FULLGROWTH)
			return new FullGenerator(factory, DEPTH);
		else
			return new GrowGenerator(factory, DEPTH);
	}

	private static Mutation getMutation(NodeFactory factory, PopulationGenerator generator, Crossover xo) {
		if(USE_POINTMUT)
			return new PointMutation(random, factory, PMUT_PER_NODE);
		else
			return new SubtreeMutation(generator, xo);
	}

	private static NodeFactory getNodeFactory(InputFileParser p) {
		if(USE_SINFACTORY)
			return new SinFactory(random, p.getNvar(), p.getNrand(), p.getMinrand(), p.getMaxrand());
		else
			return new SinFactoryEps(random, p.getNvar(), p.getNrand(), p.getMinrand(), p.getMaxrand());
	}

	private static Node bestIndividual(Node[] pop, FitnessFunction fitness) {
		return Stream.of(pop).max(
				(n1, n2) -> (int)Math.signum(fitness.evalFitness(n1)-fitness.evalFitness(n2))
			).get(); // Stream.max() returns an Optional
	}
	
}
