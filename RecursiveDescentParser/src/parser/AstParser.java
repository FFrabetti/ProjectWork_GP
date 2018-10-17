package parser;

import java.util.Stack;

import ast.*;
import exception.ParseException;
import scanner.ExpToken;
import scanner.Scanner;

// compiler: it creates an AST that can be evaluated using the visitor design pattern

public class AstParser implements ExpParser<Exp> {

	private Scanner scanner;
	private ExpToken currentToken;
	private Stack<String> stack; // debug
	
	public AstParser(Scanner scanner) {
		this.scanner = scanner;
	}

	private void nextToken() {
		currentToken = new ExpToken(scanner.hasMoreTokens() ? scanner.nextToken() : null);
	}

	private void push(String s) {
		stack.push(s);
	}
	
	public Stack<String> getDebugTrace() {
		return stack;
	}
	
	// entry-points
	@Override
	public Exp parseLine(String line) {
		scanner.readLine(line);
		return parseExp();
	}

	@Override
	public Exp parseExp() {
		currentToken = new ExpToken(null); // avoid null pointer exception
		stack = new Stack<>();
				
		Exp result = parseExpInt();
		if (result == null)
			throw new ParseException("Invalid expression");

		// top-level reached: there should be nothing afterwards
		if (currentToken.isPresent())
			throw new ParseException("Unexpected token: " + currentToken);
		
		return result;
	}

	// E ::= MONO | SEQ
	private Exp parseExpInt() {
		push("E");
		
		Exp mono = parseMono();
		return mono != null ? mono : parseSeq();
	}

	// MONO ::= T | E + T | E - T // T {(+|-) T}
	private Exp parseMono() {
		push("MONO");
		
		Exp t1 = parseTerm();
		if (t1 == null)
			return null;

		while (currentToken.isString("+") || currentToken.isString("-")) {
			String op = currentToken.getValue();
			push(op);
			
			Exp t2 = parseTerm();
			if(t2 == null)
				throw new ParseException("Unexpected token after " + op + ": " + currentToken);

			t1 = newOpNode(t1, op, t2);
		}
		return t1;
	}

	// EXPRESSIONS SEQUENCES (comma operator)
	// they are evaluated from left to right, even though they are right-associative (see below)
	// a=4, b=$a, c=$a+$b -> a == 4 && b == 4 && c == 8
	// the value of the sequence is the one of the right-most expression
	// d=(e=2, f=4) -> d == 4
	// just the last expression MAY NOT be an assignment (otherwise there would be useless values...)
	// -> right-recursive production rule, hence right-associative tree shape
	// h=(g=5, $g+2) -> h == 7
	
	// NOTE: X -> aX | e (epsilon) "effectively LL(1)"
	// SEQ ::= A S
	// S ::= , E | e
	private Exp parseSeq() {
		push("SEQ");
		
		Exp seq = parseAssign();
		if(seq == null)
			return null;
		
		if(currentToken.isString(",")) {
			push(",");
			
			Exp right = parseExpInt(); // right-recursive, ok for LL(1)
			if(right == null)
				throw new ParseException("Expected expression after ,");
			
			seq = new SeqExp(seq, right);
		}
		return seq;
	}

	// A ::= IDENT = MONO | IDENT = A
	private Exp parseAssign() {
		push("A");

		if (!currentToken.isIdentifier())
			return null;
		
		Exp leftV = new LValueExp(currentToken.getValue());
		push(currentToken.getValue());
		
		nextToken();
		if (!currentToken.isString("="))
			throw new ParseException("Missing = after left-value identifier");
		push("=");
		
		Exp mono = parseMono();
		Exp rightV = mono != null ? mono : parseAssign();
		if(rightV == null)
			throw new ParseException("Expected expression after =");
		
		return new AssignExp(leftV, rightV);
	}

	// T ::= P | T * P | T / P | T % P // P {(*|/|%) P}
	private Exp parseTerm() {
		push("T");
		
		Exp p1 = parsePow();
		if(p1 == null)
			return null;
		
		while (currentToken.isString("*") || currentToken.isString("/") || currentToken.isString("%")) {
			String op = currentToken.getValue();
			push(op);

			Exp p2 = parsePow();
			if(p2 == null)
				throw new ParseException("Unexpected token after " + op + ": " + currentToken);
			
			p1 = newOpNode(p1, op, p2);
		}
		return p1;
	}

	// P ::= F | F ^ P (right-recursive, ok for LL(1))
	private Exp parsePow() {
		push("P");
		
		Exp base = parseFactor();
		if(base == null)
			return null;
		
		if (currentToken.isString("^")) {
			push("^");

			Exp pow = parsePow(); // recursive
			if(pow == null)
				throw new ParseException("Unexpected token after ^: " + currentToken);
			
			base = newOpNode(base, "^", pow);
		}
		return base;
	}

	// F ::= (M) | $IDENT | num
	// M ::= -F | E
	private Exp parseFactor() {
		push("F");
		
		// parseFactor does the readings
		nextToken();
		if (!currentToken.isPresent())
			throw new ParseException("Expected token");
		
		Exp result;
		switch (currentToken.getValue()) {
		case "(":
			result = parseMinusExp();
			if (result == null)
				throw new ParseException("Expected inner-expression after (");
			
			if (!currentToken.isString(")"))
				throw new ParseException("Missing )");
			break;

		case "$":
			nextToken();
			if (!currentToken.isIdentifier())
				throw new ParseException("Invalid identifier after $: " + currentToken);

			push("$" + currentToken);
			result = new RValueExp(currentToken.getValue());
			break;

		default:
			if (currentToken.isNumber()) {
				push(currentToken.getValue());
				result = new NumExp(currentToken.getValue());
			} else
				// none of my business, the caller has to deal with it
				// it might be an assignment (L-value identifier)
				return null; // no further readings, currentToken still unused
		}

		nextToken();
		return result;
	}

	// M ::= -F | E
	private Exp parseMinusExp() {
		push("M");

		Exp exp = parseExpInt();
		if (exp == null) { // case -F
			if(!currentToken.isString("-"))
				throw new ParseException("Expected - (unary minus) within ()");
			push("u-");
			
			Exp innerExp = parseFactor();
			if(innerExp == null)
				throw new ParseException("Invalid inner-expression (-F)");
			
			exp = new UnaryMinusExp(innerExp);
		}
		return exp;
	}

	private OpExp newOpNode(Exp t1, String operator, Exp t2) {
		switch (operator) {
		case "+":
			return new PlusExp(t1, t2);
		case "-":
			return new MinusExp(t1, t2);
		case "*":
			return new TimesExp(t1, t2);
		case "/":
			return new DivExp(t1, t2);
		case "%":
			return new ModExp(t1, t2);
		case "^":
			return new PowExp(t1, t2);
		default:
			throw new ParseException("Unknown operator: " + operator);
		}
	}
	
}
