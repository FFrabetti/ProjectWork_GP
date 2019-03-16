package test.mockimpl;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import evolution.TimeMachine;
import examples.mockimpl.MockFactory;
import examples.mockimpl.MockFitness;
import initialization.FullGenerator;
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
import utils.RandomGenerator;
import visitor.CountVisitor;

public class TestMain {

	// using the same parameters of TinyGP
	private static final int SEED = 1;
	private static final int POPSIZE = 500;
	private static final int DEPTH = 3; // depth of the initial pop.
	private static final double CROSSOVER_PROB = 0.9;
	private static final double PMUT_PER_NODE = 0.05;
	private static final int GENERATIONS = 100; // max nr of generations
	private static final int TSIZE = 8; // tournament size

	// new:
	private static final double MUTATION_PROB = 0.02;
	// reproduction_prob = 1 - CROSSOVER_PROB - MUTATION_PROB
	private static final double FITNESS_THRESHOLD = 1-0.05;
	
	public static void main(String[] args) {
		RandomGenerator.getInstance().setSeed(SEED);
		
		NodeFactory factory = new MockFactory(0.5);
		PopulationGenerator generator = new FullGenerator(factory, DEPTH);
		FitnessFunction fitness = new MockFitness();
		SelectionMechanism selection = new TournamentSelection(fitness, TSIZE);

		// operators
		Crossover stxo = new SubtreeCrossover();
		Operator crossover = new BaseOperator(CROSSOVER_PROB,
				pop -> stxo.apply(selection.selectOne(pop), selection.selectOne(pop)));
		
		Mutation ptm = new PointMutation(factory, PMUT_PER_NODE);
		Operator mutation = new BaseOperator(MUTATION_PROB,
				pop -> ptm.mutate(selection.selectOne(pop)));

//		Mutation stm = new SubtreeMutation(generator, stxo);
//		Operator mutation = new BaseOperator(MUTATION_PROB,
//				pop -> stm.mutate(selection.selectOne(pop)));
		
		double reproduction_prob = 1 - CROSSOVER_PROB - MUTATION_PROB;
		Operator reproduction = new BaseOperator(reproduction_prob,
				pop -> selection.selectOne(pop).clone());
		
		// time machine
		Node[] initialPop = generator.generate(POPSIZE);
		TimeMachine tm = new TimeMachine(new Operator[] {crossover, mutation, reproduction});

		// termination: fitness of the best individual above a certain threshold
		Predicate<Node[]> terminationCriterion = pop -> 
				fitness.evalFitness(bestIndividual(pop, fitness))>=FITNESS_THRESHOLD;
		
		System.out.println("TimeMachine.run()");
//		Consumer<Node[]> action = pop -> {
//			PopulationAnalyser.printStats(pop);
//			PopulationAnalyser.printFitness(pop, fitness);
//			System.out.println("------------------------------------");
//		};
		int gen = tm.run(initialPop, GENERATIONS, terminationCriterion); // , action);
		System.out.println("generations = " + gen + ", isSuccess = " + tm.isSuccess(terminationCriterion));
		Node best = bestIndividual(tm.getCurrentGeneration(), fitness);
		System.out.println("best fitness = " + fitness.evalFitness(best));
		System.out.println("best node = " + best);
		
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
		best = bestIndividual(pop, fitness);
		System.out.println("BEST (f=" + fitness.evalFitness(best) + ") = " + best);

		
		// https://wiki.openoffice.org/wiki/Documentation/How_Tos/Calc:_FREQUENCY_function
		// http://wikieducator.org/OpenOffice/Calc_3/Histogram
		String prefix = TestMain.class.getName();
		long suffix = System.currentTimeMillis();
		String path = "runs/";
		
		Function<Node, String[]> funct = n -> { // for each node
			CountVisitor v = new CountVisitor();
			n.accept(v);
			
			return new String[] {
					String.valueOf(v.getDepth()),
					String.valueOf(v.getSize()),
					String.valueOf(fitness.evalFitness(n))};
		};
		String[] labels = new String[] {"depth","size","fitness"};
		
		DataExporter.createCSV(initialPop, path + prefix + suffix + "_initial.csv", funct, labels);
		DataExporter.createCSV(pop, path + prefix + suffix + "_final.csv", funct, labels);
		// REMEMBER to set an "English locale" when you open them (decimal separator . )
	}

	private static Node bestIndividual(Node[] pop, FitnessFunction fitness) {
		return Stream.of(pop).max(
				(n1, n2) -> (int)Math.signum(fitness.evalFitness(n1)-fitness.evalFitness(n2))
			).get(); // Stream.max() returns an Optional
	}
	
}
