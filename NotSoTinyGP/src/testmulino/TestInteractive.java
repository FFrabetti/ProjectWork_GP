package testmulino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import mulino.Board;
import mulino.Checker;
import mulino.MulinoAction;
import mulino.MulinoState;
import mulino.Board.Position;

public class TestInteractive {

	public static void main(String args[]) throws IOException {
		
		Board board = new Board();
		
		// put some checkers on the board
		Random rand = new Random();
		placeCheckers(Checker.WHITE, 9, board, rand);
		placeCheckers(Checker.BLACK, 4, board, rand);
		
		MulinoState state = new MulinoState(board, 0, 0); // no phase 1, no checkers still to be played
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			while (!state.isOver()) {
				System.out.println("Phase: " + state.getCurrentPhase());
				System.out.println("Player: " + state.getDutyPlayer());
				
				System.out.println(state.getBoard().niceToString());
				
				int i = 0;
				List<MulinoAction> actions = state.legitActions();
				for(MulinoAction a : actions) {
					System.out.println((++i) + ". " + a);
				}
				
				System.out.println("Action:");
				String inputAction = br.readLine();
				int index = Integer.parseInt(inputAction);
				if(index <= i)
					state = actions.get(index-1).perform(state);
				else
					throw new IllegalArgumentException("There is no action with index " + index);
			}
			
			System.out.println("------------------- \n");
			
			Checker winner;
			if(!state.hasLegitActions())
				winner = state.enemyPlayer();
			else
				winner = state.getBoard().checkers(Checker.WHITE)<3 ? Checker.BLACK : Checker.WHITE;
			
			System.out.println("Winner: " + winner);
		}
	}
	
	private static void placeCheckers(Checker c, int n, Board b, Random rand) {
		List<Position> list = b.freePositions();
		while(n > 0) {
			Position p = list.get(rand.nextInt(list.size()));
			if (b.isFree(p)) {
				b.put(p, c);
				n--;
			}
		}
	}
	
}
