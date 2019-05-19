package evolution;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import model.Node;
import operators.Operator;

public class TimeMachine {

	private Random random;
	private Operator[] operators;
	private Node[] currentGeneration; // support field for run()
	
	public TimeMachine(Random random, Operator[] operators) {
		this.random = random;
		this.operators = operators;
	}
	
	// it can be invoked manually, or automatically by run()
	public Node[] nextGeneration(Node[] pop) {
		Node[] generation = new Node[pop.length];
		
		// No negative tournament?
		// In TinyGP the individuals of a generation are gradually replaced by the ones of the next,
		// with negative tournaments used to select the "locally" worst individuals to be replaced.
		// In this way, in case of crossover, an individual may have parents from its own generation.
		
		// Differently from TinyGP, we decide to keep the "past" and the "future" generations separated
		
		// no "eager" evaluation of the fitness of new individuals, unlike TinyGP
		// (in order to keep fitness evaluation confined elsewhere)
		
		for(int i=0; i<generation.length; i++)
			generation[i] = selectRandOp().apply(pop);
		
		return generation;
	}

	private Operator selectRandOp() {
		double p = random.nextDouble();
		int i = 0;
		for(double q=0; i<operators.length && p>=q; q+=operators[i++].getOperatorRate());
		return operators[i-1];
	}
	
	public Node[] getCurrentGeneration() {
		return currentGeneration;
	}

	// see TinyGP evolve()
	public int run(Node[] initialPop, int maxGen, Predicate<Node[]> terminationCriterion, Consumer<Node[]> action) {
		currentGeneration = initialPop;

		int i; // generation index
		// the conditions order is important: we want to check the last generation as well, before exiting
		for(i=0; !isSuccess(terminationCriterion) && i<maxGen; i++) {
			currentGeneration = nextGeneration(currentGeneration);
			
			if(action != null)
				action.accept(currentGeneration); // operates via side-effects
		}
		
		return i;
	}
	
	// internally used by run() and to be used when run() == maxGen
	// did it stop because maxGen was reached (fail) or because of a success in the last generation?
	public boolean isSuccess(Predicate<Node[]> terminationCriterion) {
		return terminationCriterion.test(currentGeneration);
	}

	public int run(Node[] initialPop, int maxGen, Predicate<Node[]> terminationCriterion) {
		return run(initialPop, maxGen, terminationCriterion, null);
	}
	
	// it always returns maxGen
	public int run(Node[] initialPop, int maxGen) {
		return run(initialPop, maxGen, gen -> false);
	}
	
}
