package mulino;

import java.util.Optional;
import mulino.Board.Position;

public abstract class MulinoAction {

	private Position to;
	private Optional<Position> removeOpponent;
	
	public MulinoAction(Position to, Position removeOpponent) {
		this.to = to;
		setRemoveOpponent(removeOpponent);
	}

	public MulinoAction(Position to) {
		this(to, null);
	}
	
	public Position getTo() {
		return to;
	}

	public Optional<Position> getRemoveOpponent() {
		return removeOpponent;
	}

	private void setRemoveOpponent(Position removeOpponent) {
		this.removeOpponent = Optional.ofNullable(removeOpponent);
	}
	
	// parte comune a tutte le fasi:
	protected MulinoState finishToPerform(MulinoState newState) {
		// se ho fatto un mulino posso rimuovere una pedina nemica
		if (removeOpponent.isPresent())
			newState.removeChecker(removeOpponent.get());
		
		newState.switchDutyPlayer(); // poi toccherà all'avversario

		return newState;
	}
	
	public abstract MulinoState perform(MulinoState currentState);

	@Override
	public String toString() {
		String result = to.toString();
		
		if(removeOpponent.isPresent())
			result += " remove " + removeOpponent.get();
		
		return result;
	}
	
	public boolean isNoOp() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
