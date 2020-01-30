package mulino;

import mulino.Board.Position;

public class Phase1MulinoAction extends MulinoAction {
	
	public Phase1MulinoAction(Position to, Position removeOpponent) {
		super(to, removeOpponent);
	}

	public Phase1MulinoAction(Position to) {
		this(to, null);
	}
	
	@Override
	public MulinoState perform(MulinoState currentState) {
		MulinoState newState = ((MulinoState)currentState).clone(); // sarà molto simile al precedente

		newState.playChecker(getTo());

		return finishToPerform(newState);
	}

}
