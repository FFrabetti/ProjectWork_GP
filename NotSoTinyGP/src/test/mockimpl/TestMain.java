package test.mockimpl;
import java.util.function.Function;
import java.util.stream.Stream;

import evolution.TimeMachine;
import examples.mockimpl.MockFactory;
import examples.mockimpl.MockFitness;
import fitness.FitnessFunction;
import initialization.FullGenerator;
import initialization.PopulationGenerator;
import model.Node;
import model.NodeFactory;
import operators.Crossover;
import operators.DefaultOperator;
import operators.Mutation;
import operators.Operator;
import operators.PointMutation;
import operators.SubtreeCrossover;
import operators.SubtreeMutation;
import selection.SelectionMechanism;
import selection.TournamentSelection;
import utils.DataExporter;
import utils.PopulationAnalyser;
import utils.RandomGenerator;
import visitor.CountVisitor;

public class TestMain {

	private static final int POPSIZE = 500;

	public static void main(String[] args) {

		RandomGenerator.getInstance().setSeed(1);
		
		NodeFactory factory = new MockFactory(0.5);
		PopulationGenerator generator = new FullGenerator(factory, 3);
		FitnessFunction fitness = new MockFitness();
		SelectionMechanism selection = new TournamentSelection(fitness, 10);

		Crossover stxo = new SubtreeCrossover();
		Operator crossover = new DefaultOperator(0.9, pop -> stxo.apply(selection.selectOne(pop), selection.selectOne(pop)));
		
		Mutation ptm = new PointMutation(factory, 0.1);
		Operator mutation = new DefaultOperator(0.02, pop -> ptm.mutate(selection.selectOne(pop)));

//		Mutation stm = new SubtreeMutation(generator, stxo);
//		Operator mutation2 = new DefaultOperator(0.02, pop -> stm.mutate(selection.selectOne(pop)));
		
		Operator reproduction = new DefaultOperator(0.08, pop -> selection.selectOne(pop).clone());
		
		
		Node[] initialpop = generator.generate(POPSIZE);
		TimeMachine tm = new TimeMachine(new Operator[] {crossover, mutation, reproduction});
		
		Node[] pop = initialpop;
		int maxGen = 5;
		// termination criterion: fitness of the best individual above a certain threshold
		for(int i=1; i<=maxGen; i++) {
			if(i!=1)
				pop = tm.nextGeneration(pop);
			
			System.out.println(i + " GENERATION");
			PopulationAnalyser.printStats(pop);
			PopulationAnalyser.printFitness(pop, fitness);
			System.out.println("------------------------------------");
		}
		
		System.out.println("\n %%%%%%%%%% TimeMachine.run() %%%%%%%%%%");
		tm.run(initialpop, maxGen, gen -> false, gen -> {
			PopulationAnalyser.printStats(gen);
			PopulationAnalyser.printFitness(gen, fitness);
			System.out.println("------------------------------------");
		});
		
		// Stream.max() returns an Optional
		System.out.println("BEST =  " + 
				Stream.of(tm.getCurrentGeneration()).max(
						(n1, n2) -> (int)Math.signum(fitness.evalFitness(n1)-fitness.evalFitness(n2))));
		
		// https://wiki.openoffice.org/wiki/Documentation/How_Tos/Calc:_FREQUENCY_function
		// http://wikieducator.org/OpenOffice/Calc_3/Histogram
		Function<Node, String[]> funct = n -> {
			CountVisitor v = new CountVisitor();
			v.visit(n);
			MockFitness f = new MockFitness();
			
			return new String[] {
					String.valueOf(v.getDepth()),
					String.valueOf(v.getSize()),
					String.valueOf(f.evalFitness(n))};
		};
		String[] labels = new String[] {"depth","size","fitness"};
		DataExporter.createCSV(initialpop, "initial.csv", funct, labels);
		DataExporter.createCSV(tm.getCurrentGeneration(), "final.csv", funct, labels);
	}

}
