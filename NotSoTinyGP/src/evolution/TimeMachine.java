package evolution;

import java.util.function.Consumer;
import java.util.function.Predicate;

import model.Node;
import operators.Operator;
import utils.RandomGenerator;

public class TimeMachine {

	private Operator[] operators;
	private Node[] currentGeneration;
	
	public TimeMachine(Operator[] operators) {
		this.operators = operators;
	}
	
	public Node[] nextGeneration(Node[] pop) {
		Node[] generation = new Node[pop.length];
		
		for(int i=0; i<generation.length; i++)
			generation[i] = selectRandOp().apply(pop);
		
		return generation;
	}

	private Operator selectRandOp() {
		double p = RandomGenerator.getInstance().nextDouble();
		int i = 0;
		for(double q=0; i<operators.length && p>=q; q+=operators[i++].getOperatorRate());
		return operators[i-1];
	}
	
	public Node[] getCurrentGeneration() {
		return currentGeneration;
	}

	public boolean run(Node[] initialPop, int maxGen, Predicate<Node[]> terminationCriterion, Consumer<Node[]> action) {
		currentGeneration = initialPop;

		for(int i=0; i<maxGen; i++) {
			currentGeneration = nextGeneration(currentGeneration);
			
			if(action != null)
				action.accept(currentGeneration);
			
			if(terminationCriterion.test(currentGeneration))
				return true;
		}
		
		return false; // maxGen reached
	}
	
	public boolean run(Node[] initialPop, int maxGen, Predicate<Node[]> terminationCriterion) {
		return run(initialPop, maxGen, terminationCriterion, null);
	}
	
	public boolean run(Node[] initialPop, int maxGen) {
		return run(initialPop, maxGen, gen -> false);
	}
	
}
