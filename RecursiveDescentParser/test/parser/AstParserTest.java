package parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ast.Exp;
import exception.ParseException;
import scanner.RegexScanner;
import visitor.EvalDoubleVisitor;
import visitor.EvalIntVisitor;
import visitor.EvalVisitor;

public class AstParserTest {

	private static AstParser parser;
	
	// UTILITIES
	private static void assertDouble(double d1, Object o) {
		if(!(o instanceof Double))
			throw new NumberFormatException("Not a Double: " + o);
		
		Assert.assertEquals(d1, ((Number)o).doubleValue(), 0.00001); // equals within a positive delta
	}
	
	private void assertInteger(int i, Object o) {
		if(!(o instanceof Integer))
			throw new NumberFormatException("Not an Integer: " + o);
		
//		Assert.assertEquals(new Integer(i), (Integer)o);
		Assert.assertTrue(i == ((Integer)o).intValue());
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		parser = new AstParser(new RegexScanner());
	}
	
	@Test
	public void testAssociativity() {
		Exp ast = parser.parseLine("7-2*2-2+4*(2^3^2-(2^3)^2*8)");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(1, ev.getResult());
	}
	
	@Test
	public void testNegativePowers() {
		Exp ast = parser.parseLine("0-100 - 101^(5*((0-4)))^3");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(-100, ev.getResult());
	}

	@Test
	public void testAssignment() {
		Exp ast = parser.parseLine("x0z=y10=1+3/2-(A=2^3+2)+1*5+$A");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(7, ev.getResult());
		assertInteger(7, ev.getEnvironment().get("x0z"));
		assertInteger(7, ev.getEnvironment().get("y10"));
		assertInteger(10, ev.getEnvironment().get("A"));
	}

	@Test
	public void testNestedAssignments() {
		Exp ast = parser.parseLine("1+(x=4)*(y=2+(z=3-2*1)-4/2)+$x^$y-1+(z=$z+1)");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(10, ev.getResult());
		assertInteger(4, ev.getEnvironment().get("x"));
		assertInteger(1, ev.getEnvironment().get("y"));
		assertInteger(2, ev.getEnvironment().get("z"));
	}

	@Test
	public void testUnaryMinus() {
		Exp ast = parser.parseLine("(-4)+2*(-(1-2))+(y=(-2))+(-$y)+(-($y+1))");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(-1, ev.getResult());
		assertInteger(-2, ev.getEnvironment().get("y"));
	}
	
	@Test
	public void testSequence() {
		Exp ast = parser.parseLine("a=4, b=$a, c=$a+$b");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(8, ev.getResult());
		assertInteger(4, ev.getEnvironment().get("a"));
		assertInteger(4, ev.getEnvironment().get("b"));
		assertInteger(8, ev.getEnvironment().get("c"));
	}
	
	@Test
	public void testAssignmentsSequence() {
		Exp ast = parser.parseLine("h=(i=4+4, 7)");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(7, ev.getResult());
		assertInteger(7, ev.getEnvironment().get("h"));
		assertInteger(8, ev.getEnvironment().get("i"));
	}
	
	@Test
	public void testMultiAssignmentInSequence() {
		Exp ast = parser.parseLine("h=i=4+4, 7");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(7, ev.getResult());
		assertInteger(8, ev.getEnvironment().get("h"));
		assertInteger(8, ev.getEnvironment().get("i"));
	}
	
	@Test
	public void testUnaryMinusAssignment() {
		Exp ast = parser.parseLine("3+((-1)+11)+(-(a=2))");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(11, ev.getResult());
		assertInteger(2, ev.getEnvironment().get("a"));
	}
	
	@Test(expected = ParseException.class)
	public void testUnaryMinusAssignmentFail() {
		Exp ast = parser.parseLine("3+(-a=2)");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
	}
	
	@Test
	public void testModulo() {
		Exp ast = parser.parseLine("2+16%9%3");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(3, ev.getResult());
		
		ast = parser.parseLine("2+16.5%9%3.5");
		ev = new EvalVisitor();
		ast.accept(ev);
		
		assertDouble(2.5, ev.getResult());
	}
	
	@Test
	public void testDoubleSum() {
		Exp ast = parser.parseLine("2.56+2.5");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertDouble(5.06, ev.getResult());
	}
	
	@Test
	public void testDoubleDiv() {
		Exp ast = parser.parseLine("5.0/2");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertDouble(2.5, ev.getResult());
	}

	@Test
	public void testIntPowers() {
		Exp ast = parser.parseLine("2^(-2)");
		EvalVisitor ev = new EvalVisitor();
		ast.accept(ev);
		
		assertDouble(0.25, ev.getResult());
		
		ast = parser.parseLine("(-(2^2))");
		ev = new EvalVisitor();
		ast.accept(ev);
		
		assertInteger(-4, ev.getResult());
	}
	
	@Test
	public void testPowersEvalInt() {
		Exp ast = parser.parseLine("2^(-1)");
		EvalIntVisitor eiv = new EvalIntVisitor();
		ast.accept(eiv);
		
		assertInteger(0, eiv.getResult());
		
		ast = parser.parseLine("2^(-2)");
		eiv = new EvalIntVisitor();
		ast.accept(eiv);
		
		assertInteger(0, eiv.getResult());
		
		ast = parser.parseLine("(-2)^(-1)");
		eiv = new EvalIntVisitor();
		ast.accept(eiv);
		
		assertInteger(0, eiv.getResult());
	}
	
	@Test
	public void testPowersEvalDouble() {
		Exp ast = parser.parseLine("2^(-1)");
		EvalDoubleVisitor edv = new EvalDoubleVisitor();
		ast.accept(edv);
		
		assertDouble(0.5, edv.getResult());
		
		ast = parser.parseLine("2^(-2)");
		edv = new EvalDoubleVisitor();
		ast.accept(edv);
		
		assertDouble(0.25, edv.getResult());
		
		ast = parser.parseLine("(-2)^(-1)");
		edv = new EvalDoubleVisitor();
		ast.accept(edv);
		
		assertDouble(-0.5, edv.getResult());
	}
	
}
