package mulino;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

	private Position[] positions = new Position[] {
			new Position(-3, -3), new Position(-3, 0), new Position(-3, 3),
			new Position(-2, -2), new Position(-2, 0), new Position(-2, 2),
			new Position(-1, -1), new Position(-1, 0), new Position(-1, 1),
			new Position(0, -3),  new Position(0, -2), new Position(0, -1),
			new Position(0, 1),   new Position(0, 2),  new Position(0, 3),
			new Position(1, -1),  new Position(1, 0),  new Position(1, 1),
			new Position(2, -2),  new Position(2, 0),  new Position(2, 2),
			new Position(3, -3),  new Position(3, 0),  new Position(3, 3)
	};

	private Map<Position, Checker> map;
	private int[] checkers; // potrei contarle ogni volta, ma è più veloce averle

	// for withMove and undoMove
	private Position lastFrom;
	private Position lastTo;

	public Board(Map<Position,Checker> map, int wCheckers, int bCheckers) {
		this.map = map;
		checkers = new int[] {wCheckers, bCheckers};
	}
	
	public Board() {
		this(new HashMap<>(), 0, 0);
		
		// empty positions simply aren't in the map!
		// for(Position p : positions)
		// map.put(p, Checker.EMPTY);
	}
	
	public Position[] positions() {
		return positions;
	}

	public int checkers(Checker checker) {
		return checkers[checker.ordinal()];
	}

	private void incremCheckers(Checker checker) {
		checkers[checker.ordinal()]++;
	}

	private void decremCheckers(Checker checker) {
		checkers[checker.ordinal()]--;
	}

	public List<Position> freePositions() {
		return Stream.of(positions).filter(this::isFree).collect(Collectors.toList());
	}

	public boolean isFree(Position p) {
		return !map.containsKey(p);
	}
	
	public List<Position> getPositions(Checker player) {
//		return map.entrySet().stream().filter(e -> e.getValue()==player).map(Entry::getKey).collect(Collectors.toList());
		return map.keySet().stream().filter(p -> map.get(p)==player).collect(Collectors.toList());
	}
	
	// more efficient methods
	// they avoid the creation of a List<Position>, if I just need to iterate on each element
	public Stream<Position> positions(Predicate<? super Position> predicate) {
		return Stream.of(positions).filter(predicate);
	}
	
	public Stream<Position> positions(Checker player) {
		return map.keySet().stream().filter(p -> map.get(p)==player);
	}
	
	public Board withMove(Position p, Checker player) {
		lastTo = p; // save it, so it can be undone later
		map.put(p, player);
		return this;
	}

	public Board withMove(Position from, Position to) {
		lastFrom = from;
		lastTo = to;
		move(from, to);
		return this;
	}
	
	public void undoMove() {
		if(lastFrom != null)
			move(lastTo, lastFrom);
		else if(lastTo != null)
			map.remove(lastTo);
		
		lastFrom = null;
		lastTo = null;
	}

	public Checker getChecker(Position p) {
		return map.getOrDefault(p, Checker.EMPTY);
	}

	private Position getPos(int x, int y) {
		int index = 0;
		if(x == 0)
			index = y>0 ? y+11 : y+12;				// 9 (-3+12), 10 (-2+12), 11 (-1+12), 12 (1+11), 13 (2+11), 14 (3+11)
		else if(x < 0)								// x = -3           , -2               , -1
			index = (x+3)*3 + sign0(y) + 1; 		// 0,1,2 (0+[0,1,2]), 3,4,5 (3+[0,1,2]), 6,7,8 (6+[0,1,2])
		else // x > 0                               // x = 1                   , 2                       , 3
			index = (x-3)*3 + 21 + sign0(y) + 1; 	// 15,16,17 (-6+21+[0,1,2]), 18,19,20 (-3+21+[0,1,2]), 21,22,23 (21+[0,1,2])
		
		return positions[index];
	}
	
	private Checker getChecker(int x, int y) {
		return getChecker(getPos(x, y));
	}

	private boolean halfColumn(int sign, Checker checker) {
		for (int i = 1; i <= 3; i++) // tutti e 3 i livelli
			if(getChecker(0, i*sign) != checker) // devono esserci 3 pedine uguali
				return false;
		
		return true;
	}
	
	private boolean halfRow(int sign, Checker checker) {
		for (int i = 1; i <= 3; i++) // tutti e 3 i livelli
			if(getChecker(i*sign, 0) != checker) // devono esserci 3 pedine uguali
				return false;
		
		return true;
	}
	
	private int sign0(int n) {
		return n == 0 ? 0 : sign(n);
	}
	
	private int sign(int n) {
		return n>=0 ? 1 : -1;
	}

	// data una posizione p, ritorna le 6 posizioni in cui ci può essere un mulino:
	// indici 0-2: posizioni nella riga (inclusa p)
	// indici 3-5: posizioni nella colonna (inclusa p)
	public Position[] mulinoPositions(Position p) {
		int x = p.getX();
		int y = p.getY();
		Position[] res = new Position[6];
		
		for(int i=0; i<3; i++) {
			if (x != 0 && y != 0) { // punti sulle diagonali (vertici)
				res[i] = getPos((-x)+i*x, y);		// riga, stessa y
				res[i+3] = getPos(x, (-y)+i*y);		// col, stessa x
			}
			else if (x == 0) {	// asse y
				res[i] = getPos((-y)+i*y, y);			// riga, stessa y
				res[i+3] = getPos(0, (i+1)*sign(y));	// col, stessa x
			}
			else {	// asse x (y == 0)
				res[i] = getPos((i+1)*sign(x), 0);	// riga, stessa y
				res[i+3] = getPos(x, (-x)+i*x);		// col, stessa x
			}
		}
		
		return res;
	}
	
	// mi dice se la posizione "p" fa parte di un mulino
	public boolean isInMulino(Position p) {
		int x = p.getX();
		int y = p.getY();
		Checker checker = map.get(p);
		boolean res = false;
		
		if (x != 0 && y != 0) // punti sulle diagonali (vertici)
			res = (getChecker(-x, y) == checker && getChecker(0, y) == checker) || // riga (x 0 -x, stessa y)
					(getChecker(x, -y) == checker && getChecker(x, 0) == checker);  // colonna (stessa x, y 0 -y)
		else if (x == 0)	// asse y
			res = (getChecker(y, y) == checker && getChecker(-y, y) == checker) || // pt simm risp asse y
					halfColumn(sign(y), checker);
		else // asse x (y == 0)
			res = (getChecker(x, x) == checker && getChecker(x, -x) == checker) || // pt simm risp asse x
					halfRow(sign(x), checker);
		
		undoMove();
		return res;
	}

	public boolean isInMulino(Position p, List<Position> list) {
		int x = p.getX();
		int y = p.getY();
		Checker checker = map.get(p);
		
		if (x != 0 && y != 0) // punti sulle diagonali (vertici)
			return (getChecker(-x, y) == checker && getChecker(0, y) == checker // riga (x 0 -x, stessa y)
					&& list.add(getPos(-x, y)) && list.add(getPos(0, y))) ||
					(getChecker(x, -y) == checker && getChecker(x, 0) == checker // colonna (stessa x, y 0 -y)
					&& list.add(getPos(x, -y)) && list.add(getPos(x, 0)));
		else if (x == 0)	// asse y
			return (getChecker(y, y) == checker && getChecker(-y, y) == checker // pt simm risp asse y
					&& list.add(getPos(y, y)) && list.add(getPos(-y, y))) ||
					(halfColumn(sign(y), checker) && addColumn(y, list));
		else // asse x (y == 0)
			return (getChecker(x, x) == checker && getChecker(x, -x) == checker // pt simm risp asse x
					&& list.add(getPos(x, x)) && list.add(getPos(x, -x))) ||
					(halfRow(sign(x), checker) && addRow(x, list));
	}

	private boolean addColumn(int y, List<Position> list) {
		for (int i = 1; i <= 3; i++)
			if(i!=Math.abs(y)) // the original position is added by the calling method
				list.add(getPos(0, i*sign(y)));
		return true;
	}

	private boolean addRow(int x, List<Position> list) {
		for (int i = 1; i <= 3; i++)
			if(i!=Math.abs(x)) // the original position is added by the calling method
				list.add(getPos(i*sign(x), 0));
		return true;
	}

	@Override
	public Board clone() {
		Board result = new Board();
		
		result.map.putAll(this.map);
		result.checkers[0] = this.checkers[0];
		result.checkers[1] = this.checkers[1];
		
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO: simmetrie scacchiera
		return o instanceof Board && ((Board)o).map.equals(this.map);
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	public void put(Position to, Checker player) {
		map.put(to, player);
		incremCheckers(player);
	}

	public void move(Position from, Position to) {
		map.put(to, map.remove(from));
	}

	public void remove(Position pos) {
		decremCheckers(map.remove(pos));
	}
	
	/*
	 * una posizione ha come adiacenti:
	 * - spigolo/diagonale 		-> (x,0), (0,y) proiezioni sugli assi
	 * - sull'asse x		 	-> (x+1,0), (x-1,0), (x,x), (x,-x)
	 * - sull'asse y			-> (0,y+1), (0,y-1), (y,y), (-y,y)
	 */
	public List<Position> adiacent(Position p) {
		int x = p.getX();
		int y = p.getY();
		List<Position> list = new LinkedList<>();
	
		if(y==0) { // asse x
			if(isValidCoordinate(x+1))
				list.add(getPos(x+1, 0));
			if(isValidCoordinate(x-1))
				list.add(getPos(x-1, 0));
			list.add(getPos(x, x));
			list.add(getPos(x, -x));
		}
		else if(x==0) { // asse y
			if(isValidCoordinate(y+1))
				list.add(getPos(0, y+1));
			if(isValidCoordinate(y-1))
				list.add(getPos(0, y-1));
			list.add(getPos(y, y));
			list.add(getPos(-y, y));
		}
		else { // diagonali
			list.add(getPos(x, 0));
			list.add(getPos(0, y));
		}
		
		return list;
	}

//	private void tryAddList(int i, int j, List<Position> list) {
//		try {
//			list.add(new Position(i, j));
//		} catch(Exception e) {}
//	}
	
	// sugli assi, verifico la validità della coordinata != 0
	private boolean isValidCoordinate(int k) {
		return k!=0 && k<=3 && k>=-3;
	}

	public List<Position> freeAdiacent(Position p) {
		return adiacent(p).stream().filter(this::isFree).collect(Collectors.toList());
	}
	
	public boolean hasFreeAdiacend(Position p) {
		return adiacent(p).stream().filter(this::isFree).anyMatch(e -> true); 
	}
	
	// more efficient version
//	public Stream<Position> freeAdiacentStream(Position p) {
//		return adiacent(p).stream().filter(this::isFree);
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Board:\n");
		map.forEach((Position p, Checker c) -> sb.append(p + ": " + c + "\n"));
		return sb.toString();
	}
	
	// "en fin, la dernière version"
	public String niceToString() {
		StringBuffer result = new StringBuffer();
		result.append(" 3 " + getChecker(-3,3) + "--------" + getChecker(0,3) + "--------" + getChecker(3,3) + "\n");
		result.append(" 2 |  " + getChecker(-2,2) + "-----" + getChecker(0,2) + "-----" + getChecker(2,2) + "  |\n");
		result.append(" 1 |  |  " + getChecker(-1,1) + "--" + getChecker(0,1) + "--" + getChecker(1,1) + "  |  |\n");
		result.append(" 0 " + getChecker(-3,0) + "--" + getChecker(-2,0) + "--" + getChecker(-1,0) + "     "
				+ getChecker(1,0) + "--" + getChecker(2,0) + "--" + getChecker(3,0) + "\n");
		result.append("-1 |  |  " + getChecker(-1,-1) + "--" + getChecker(0,-1) + "--" + getChecker(1,-1) + "  |  |\n");
		result.append("-2 |  " + getChecker(-2,-2) + "-----" + getChecker(0,-2) + "-----" + getChecker(2,-2) + "  |\n");
		result.append("-3 " + getChecker(-3,-3) + "--------" + getChecker(0,-3) + "--------" + getChecker(3,-3) + "\n");
		result.append("  -3 -2 -1  0  1  2  3\n");
		return result.toString();
	}
	
	public class Position {

		private int x;
		private int y;
		
		public Position(int x, int y) {
//			int absX = Math.abs(x);
//			int absY = Math.abs(y);
//			if((x==0 && y==0) || absX>3 || absY>3 || (x*y!=0 && absX!=absY))
//				throw new IllegalArgumentException("Invalid position: " + x + "," + y);
				
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Position))
				return false;
			
			Position p = (Position)o;	
			return this.x==p.x && this.y==p.y;
		}
		
		@Override
		public int hashCode() {
			return x ^ y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
		
	}
	
}
