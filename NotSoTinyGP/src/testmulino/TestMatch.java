package testmulino;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import examples.regression.EvalVisitor;
import model.Node;
import mulino.Board;
import mulino.Board.Position;
import visitor.CountVisitor;
import mulino.Checker;
import mulino.MulinoAction;
import mulino.MulinoState;
import mulino.Phase;

public class TestMatch {

	public static final int maxMoves = 1;
	
	/**
	 * - Select players from their population (see co-evolution)
	 * - Choose number of checkers on the board in [4-9] and place them
	 * - Play at most maxMoves or until: the game is over or phase final
	 * - If the match is over: the loser gets 0 checkers
	 * - Assign to each player a score as the difference in the number of checkers
	 */
	public static void main(String args[]) {

		Node w = null;
		Node b = null;

		Board board = new Board();

		// put some checkers on the board
		Random rand = new Random();
		int nr = rand.nextInt(9 - 4 + 1) + 4; // [0-5] + 4 = [4-9]
		placeCheckers(Checker.WHITE, nr, board, rand);
		placeCheckers(Checker.BLACK, nr, board, rand);

		MulinoState state = new MulinoState(board, 0, 0); // no phase 1, no checkers still to be played

		int movesCount = 0;
		while (!state.isOver() && movesCount < maxMoves && state.getCurrentPhase() != Phase.FINAL) {
			movesCount++;
			
			System.out.println("Phase: " + state.getCurrentPhase());
			System.out.println("Player: " + state.getDutyPlayer());
			System.out.println(state.getBoard().niceToString());

			// look ahead of 1 move: perform all possible actions, evaluate future states and pick the best			
			List<MulinoAction> actions = state.legitActions(); // not empty, game not over
			Double bestQuality = Double.MIN_VALUE;
			MulinoState bestState = null;
			for (MulinoAction a : actions) {
				MulinoState futureState = a.perform(state); // the state is cloned first
				Double stateQuality = evalState(futureState, state.getDutyPlayer(), state.getDutyPlayer() == Checker.WHITE ? w : b);
				
				if(stateQuality > bestQuality) {
					bestQuality = stateQuality;
					bestState = futureState;
				}
			}

			state = bestState;
		}

		System.out.println("------------------- \n");

		int nrCheckersW = state.getBoard().checkers(Checker.WHITE);
		int nrCheckersB = state.getBoard().checkers(Checker.BLACK);
		
		if(state.isOver()) {
			Checker loser = state.hasLegitActions() ? state.enemyPlayer() : state.getDutyPlayer();
			if(loser == Checker.WHITE)
				nrCheckersW = 0;
			else
				nrCheckersB = 0;
		}
		
		System.out.println("Score " + Checker.WHITE + ": " + (nrCheckersW-nrCheckersB));
		System.out.println("Score " + Checker.BLACK + ": " + (nrCheckersB-nrCheckersW));
	}

	private static Double evalState(MulinoState futureState, Checker dutyPlayer, Node node) {
		Board board = futureState.getBoard();
		
		int nmen = board.checkers(dutyPlayer);
		int nmen_o = board.checkers(futureState.getDutyPlayer());
		
		int nmorris = 0;
		int npairs = 0;
		Set<Position> freeAd = new HashSet<>();
		Set<Position> inMorrisSet = new HashSet<>();
		for(Position p : board.getPositions(dutyPlayer)) {
			List<Position> ad = board.freeAdiacent(p);
			freeAd.addAll(ad);
			for(Position adp : ad) {
				List<Position> inMulinoList = new LinkedList<>();
				if(board.withMove(adp, dutyPlayer).isInMulino(adp, inMulinoList) && inMulinoList.contains(p))
					npairs++;
				board.undoMove();
			}
			
			List<Position> alreadyCountedMorris = new LinkedList<>();
			if (!inMorrisSet.contains(p) && board.isInMulino(p, alreadyCountedMorris)) {
				nmorris++;
				inMorrisSet.add(p);
				inMorrisSet.addAll(alreadyCountedMorris);
			}
		}
		int nfree = freeAd.size();
		
		int nmorris_o = 0;
		int npairs_o = 0;
		freeAd = new HashSet<>();
		inMorrisSet = new HashSet<>();
		for(Position p : board.getPositions(futureState.getDutyPlayer())) {
			List<Position> ad = board.freeAdiacent(p);
			freeAd.addAll(ad);
			for(Position adp : ad) {
				List<Position> inMulinoList = new LinkedList<>();
				if(board.withMove(adp, futureState.getDutyPlayer()).isInMulino(adp, inMulinoList) && inMulinoList.contains(p))
					npairs_o++;
				board.undoMove();
			}
			
			List<Position> alreadyCountedMorris = new LinkedList<>();
			if (!inMorrisSet.contains(p) && board.isInMulino(p, alreadyCountedMorris)) {
				nmorris_o++;
				inMorrisSet.add(p);
				inMorrisSet.addAll(alreadyCountedMorris);
			}
		}
		int nfree_o = freeAd.size();
		
//		System.out.println(board.niceToString());
//		System.out.println("Nr checkers: " + nmen + " " + nmen_o);
//		System.out.println("Nr morris: " + nmorris + " " + nmorris_o);
//		System.out.println("Nr pairs: " + npairs + " " + npairs_o);
//		System.out.println("Nr free: " + nfree + " " + nfree_o);
		
		Map<String,Double> env = new HashMap<>();
		env.put("x0", (double) nmen);			// Nmen
		env.put("x1", (double) nmen_o);			// Nmen_o
		env.put("x2", (double) nmorris);		// Nmorris
		env.put("x3", (double) nmorris_o);		// Nmorris_o
		env.put("x4", (double) npairs);			// Npairs
		env.put("x5", (double) npairs_o);		// Npairs_o
		env.put("x6", (double) nfree);			// Nfree
		env.put("x7", (double) nfree_o);		// Nfree_o
		
		int vari = 8;
		for(Position p : board.positions()) {
			Checker c = board.getChecker(p);
			double val = -1;
			if(c == dutyPlayer)
				val = 1;
			else if (c == Checker.EMPTY)
				val = 0;
			
			env.put("x" + vari, val);
			vari++;
		}
		
//		CountVisitor cv = new CountVisitor();
//		node.accept(cv);
//		System.out.println("size = " + cv.getSize() + ", depth = " + cv.getDepth());
		
		EvalVisitor ev = new EvalVisitor(env);
		node.accept(ev);
//		System.out.println("value = " + ev.getResult());
		
		return ev.getResult();
	}

	private static void placeCheckers(Checker c, int n, Board b, Random rand) {
		List<Position> list = b.freePositions();
		while (n > 0) {
			Position p = list.get(rand.nextInt(list.size()));
			if (b.isFree(p)) {
				b.put(p, c);
				n--;
			}
		}
	}

}
