package mulino;

import mulino.Board.Position;

public class Phase23MulinoAction extends MulinoAction {

	private Position from;
	private Phase phase; // for conversions to Phase2Action and PhaseFinalAction
	
	public Phase23MulinoAction(Position from, Position to, Position removeOpponent, Phase phase) {
		super(to, removeOpponent);
		this.from = from;
		this.phase = phase;
	}

	public Phase23MulinoAction(Position from, Position to, Phase phase) {
		this(from, to, null, phase);
	}
	
	public Position getFrom() {
		return from;
	}

	public Phase getPhase() {
		return phase;
	}

	@Override
	public MulinoState perform(MulinoState currentState) {
		MulinoState newState = ((MulinoState)currentState).clone(); // sarà molto simile al precedente

		newState.moveChecker(from, getTo());

		return finishToPerform(newState);
	}

	@Override
	public String toString() {
		return from + " -> " + super.toString();
	}
	
}
