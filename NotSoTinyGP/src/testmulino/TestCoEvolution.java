package testmulino;

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
import operators.PruneMutation;
import operators.SubtreeCrossover;
import selection.FitnessFunction;
import selection.SelectionMechanism;
import selection.TournamentSelection;
import utils.DataExporter;
import utils.Launchable;
import utils.Launcher;
import utils.PopulationAnalyser;
import visitor.CountVisitor;

public class TestCoEvolution implements Launchable {

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
//	private static final double FITNESS_DELTA = 0.05; // ok if fitness >= maxFitness-DELTA
//	private static final String FILE_NAME = "resources/polynomial-data.txt";
	private static final int MINDEPTH_PRUNE = 100;
	private static final double PRUNE_PROB = 0.5;
	
	// args: resources/polynomial-data.txt 2 polynomial.properties
	public static void main(String[] args) {
		if(args.length == 0)
			args = new String[] {"anything", "2", "mulino.properties"};
		
		Launcher.launch(args, new TestCoEvolution());
	}

	private static Node bestIndividual(Node[] pop, FitnessFunction fitness) {
		return Stream.of(pop).max((n1, n2) -> (int) Math.signum(fitness.evalFitness(n1) - fitness.evalFitness(n2)))
				.get(); // Stream.max() returns an Optional
	}

	@Override
	public void launch(Properties properties) {
		Random random = (Random) properties.get("random");

		double pTerm = getDouble(properties, "pTerm", TERMINAL_PROB);
		
		NodeFactory factory = new DoubleOpNodeFactoryEpsilon(random,
				getInt(properties, "nvar", 8+24), getInt(properties, "nrand", 64),
				getDouble(properties, "minrand", -5), getDouble(properties, "maxrand", 5),
				pTerm);

		int mindepth = getInt(properties, "mindepth", DEPTH);
		int maxdepth = getInt(properties, "maxdepth", DEPTH);
		PopulationGenerator generator = new RampedHalfAndHalfGenerator(random, factory, mindepth, maxdepth);

		int nMatches = getInt(properties, "nMatches", 20);
		int maxMoves = getInt(properties, "maxMoves", 40);
		
		int tsize = getInt(properties, "tsize", TSIZE);

		int popsize = getInt(properties, "popsize", POPSIZE);
		Node[] initialPopW = generator.generate(popsize);
		Node[] initialPopB = generator.generate(popsize);
		
		MulinoFitness fitnessM = new MulinoFitness(initialPopW, initialPopB, nMatches, maxMoves, random);
		SelectionMechanism selectionM = new TournamentSelection(random, fitnessM, tsize);
		
		// operators
		double pCrossover = getDouble(properties, "crossover_prob", CROSSOVER_PROB);
		Crossover stxo = new SubtreeCrossover(random);
		Operator crossover = new BaseOperator(pCrossover,
				pop -> stxo.apply(selectionM.selectOne(pop), selectionM.selectOne(pop)));

		double pMutNode = getDouble(properties, "pmut_per_node", PMUT_PER_NODE);
		double pMutation = getDouble(properties, "mutation_prob", MUTATION_PROB);
		Mutation ptm = new PointMutation(random, factory, pMutNode);
//		Operator mutation = new BaseOperator(pMutation, pop -> ptm.mutate(selection.selectOne(pop)));
		int minDepthPrune = getInt(properties, "mindepth_prune", MINDEPTH_PRUNE);
		double pPrune = getDouble(properties, "pPrune", PRUNE_PROB);
		Mutation prm = new PruneMutation(random, minDepthPrune, pPrune, factory);
		Operator mutation = new BaseOperator(pMutation, pop -> {
			Node n = selectionM.selectOne(pop);
			
			CountVisitor cv = new CountVisitor();
			n.accept(cv);
			if(cv.getDepth() < minDepthPrune)
				return ptm.mutate(n);
			else
				return prm.mutate(n);
		});

		double pReproduction = 1 - pCrossover - pMutation;
		Operator reproduction = new BaseOperator(pReproduction, pop -> selectionM.selectOne(pop).clone());		
		
		List<MulinoAnalyser> listPAW = new LinkedList<>();
		List<MulinoAnalyser> listPAB = new LinkedList<>();
		
		System.out.println("TimeMachine.run()");
		Consumer<Node[]> actionW = pop -> {
			fitnessM.setWhite();
			MulinoAnalyser pa = new MulinoAnalyser(pop, fitnessM);
			pa.printStats();
			pa.printFitness();
			System.out.println("------------------------------------");
			
			// for each generation, save min/max/av. depth, size, fitness
			listPAW.add(pa);
		};
		Consumer<Node[]> actionB = pop -> {
			fitnessM.setBlack();
			MulinoAnalyser pa = new MulinoAnalyser(pop, fitnessM);
			pa.printStats();
			pa.printFitness();
			System.out.println("------------------------------------");
			
			// for each generation, save min/max/av. depth, size, fitness
			listPAB.add(pa);
		};
		actionW.accept(initialPopW); // generation 0
		actionB.accept(initialPopB); // generation 0
		
		int generations = getInt(properties, "generations", GENERATIONS);
		
		Node[] currentGenerationW = initialPopW;
		Node[] currentGenerationB = initialPopB;
		
		Operator[] operators = new Operator[] { crossover, mutation, reproduction };
		
		int i; // generation index
		for(i=0; i<generations; i++) {
			Node[] generationW = new Node[currentGenerationW.length];
			Node[] generationB = new Node[currentGenerationB.length];
			
			for(int j=0; j<generationW.length; j++) {
				fitnessM.setWhite();
				generationW[j] = selectRandOp(operators, random).apply(currentGenerationW);
				
				fitnessM.setBlack();
				generationB[j] = selectRandOp(operators, random).apply(currentGenerationB);
			}

			currentGenerationW = generationW;
			currentGenerationB = generationB;
			
			// new fitness function
			fitnessM.reStart(currentGenerationW, currentGenerationB);
			
			System.out.println("Gen " + (i+1));
			actionW.accept(currentGenerationW); // operates via side-effects
			actionB.accept(currentGenerationB); // operates via side-effects
		}
		
		// write run information on file
		System.out.println("\n");
		long timestamp = System.currentTimeMillis();
		String path = "runs/" + TestCoEvolution.class.getSimpleName() + "_" + timestamp;
//		createCSV(initialPop, path + "_initial", fitness);
		fitnessM.setWhite();
		createCSV(currentGenerationW, path + "_finalW", fitnessM);
		fitnessM.setBlack();
		createCSV(currentGenerationB, path + "_finalB", fitnessM);
		
		createPropertiesFile(properties, path); // save snapshot for future reference
//		createResultFile(gen, tm.isSuccess(terminationCriterion), best, fitness.evalFitness(best), path);
		createSummaryCSV(listPAW, path+"W");
		createSummaryCSV(listPAB, path+"B");
	}

	private Operator selectRandOp(Operator[] operators, Random random) {
		double p = random.nextDouble();
		int i = 0;
		for(double q=0; i<operators.length && p>=q; q+=operators[i++].getOperatorRate());
		return operators[i-1];
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

	private void createSummaryCSV(List<MulinoAnalyser> listPA, String path) {
		String[][] lines = new String[listPA.size()][];
		int i = 0;
		for(MulinoAnalyser pa : listPA) {
			List<String> list = new LinkedList<>();
			list.add(String.valueOf(i));
			list.add(String.valueOf(pa.getMinDepth()));
			list.add(String.valueOf(pa.getMaxDepth()));
			list.add(String.valueOf(pa.getAvDepth()));
			list.add(String.valueOf(pa.getMinSize()));
			list.add(String.valueOf(pa.getMaxSize()));
			list.add(String.valueOf(pa.getAvSize()));
			list.add(String.valueOf(pa.getMaxFitness()));
			list.add(String.valueOf(pa.getAvFitness()));
			
			for(int j=0; j<pa.getFrequency().length; j++) {
				list.add(String.valueOf(pa.getFrequency()[j]));
				list.add(String.valueOf(pa.getMindepth()[j]));
				list.add(String.valueOf(pa.getAvgdepth()[j]));
			}
			
			lines[i] = list.toArray(new String[0]);
			i++;
		}
		
		List<String> header = new LinkedList<>();
		header.add("generation");
		header.add("minDepth");
		header.add("maxDepth");
		header.add("avDepth");
		header.add("minSize");
		header.add("maxSize");
		header.add("avSize");
		header.add("maxFitness");
		header.add("avFitness");
		
		for(int j=0; j<listPA.get(0).getFrequency().length; j++) {
			header.add("x"+j+"_f");
			header.add("x"+j+"_mind");
			header.add("x"+j+"_avgd");
		}
		
		DataExporter.createCSV(lines, path + "_summary.csv", header.toArray(new String[0]));
	}

}
