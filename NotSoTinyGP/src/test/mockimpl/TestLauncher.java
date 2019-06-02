package test.mockimpl;

import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import evolution.TimeMachine;
import examples.mockimpl.MockFactory;
import examples.mockimpl.MockFitness;
import initialization.GrowGenerator;
import initialization.PopulationGenerator;
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
import utils.Launchable;
import utils.Launcher;
import utils.PopulationAnalyser;
import static utils.Launcher.getDouble;
import static utils.Launcher.getInt;

public class TestLauncher implements Launchable {

	// using the same parameters of TinyGP
	private static final int POPSIZE = 100000;
	private static final int DEPTH = 5;
	private static final double CROSSOVER_PROB = 0.9;
	private static final double PMUT_PER_NODE = 0.05;
	private static final int GENERATIONS = 100;
	private static final int TSIZE = 2;

	// new:
	private static final double TERMINAL_PROB = 0.5;
	private static final int TARGET_LEAVES = 16;
	private static final double MUTATION_PROB = 0.08;
	// reproduction_prob = 1 - CROSSOVER_PROB - MUTATION_PROB
	private static final double FITNESS_DELTA = 0.05; // ok if fitness >= maxFitness-DELTA

	// args: configFile 1 mockimpl.properties
	public static void main(String[] args) {
		Launcher.launch(args, new TestLauncher());
	}

	private static Node bestIndividual(Node[] pop, FitnessFunction fitness) {
		return Stream.of(pop).max((n1, n2) -> (int) Math.signum(fitness.evalFitness(n1) - fitness.evalFitness(n2)))
				.get(); // Stream.max() returns an Optional
	}

	@Override
	public void launch(Properties properties) {
		Random random = (Random) properties.get("random");

		double pTerm = getDouble(properties, "pTerm", TERMINAL_PROB);
		NodeFactory factory = new MockFactory(random, pTerm);

		int depth = getInt(properties, "depth", DEPTH);
		PopulationGenerator generator = new GrowGenerator(factory, depth);

		int target = getInt(properties, "target", TARGET_LEAVES);
		FitnessFunction fitness = new MockFitness(target);

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

		System.out.println("TimeMachine.run()");
		Consumer<Node[]> action = pop -> {
			PopulationAnalyser.printStats(pop);
			PopulationAnalyser.printFitness(pop, fitness);
			System.out.println("------------------------------------");
		};
		int generations = getInt(properties, "generations", GENERATIONS);
		int gen = tm.run(initialPop, generations, terminationCriterion, action);

		System.out.println("\nRESULT");
		System.out.println("generation = " + gen + ", isSuccess = " + tm.isSuccess(terminationCriterion));
		Node best = bestIndividual(tm.getCurrentGeneration(), fitness);
		System.out.println("best fitness = " + fitness.evalFitness(best));
		System.out.println("best node = " + best);
	}

}
