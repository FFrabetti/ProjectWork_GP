import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ast.Exp;
import parser.AstParser;
import parser.ExpParser;
import scanner.RegexScanner;
import visitor.EvalVisitor;

public class TestMain {

	// Top-down recursive descent parser:
	// it uses the stack of the JVM to implement a PDA:
	// - automatic push/pop
	// - state maintained by local variables (activation records on the stack)
	// - a method for each meta-symbol (sub-language)

	// chars -> Scanner -> tokens
	// tokens -> Parser -> result: value (interpreter) / AST (compiler)

	public static void main(String[] args) throws IOException {		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		ExpParser<Exp> astParser = new AstParser(new RegexScanner());
		do {
			System.out.println("Expression:");
			line = br.readLine();

			if (line != null) {
				try {
					Exp ast = astParser.parseLine(line);
					System.out.println("AST: " + ast);

					EvalVisitor ev = new EvalVisitor();
					ast.accept(ev);
					System.out.println("EvalVisitor: " + ev.getResult() + ", ENV: " + ev.getEnvironment());

					System.out.println("AST Debug trace: " + ((AstParser)astParser).getDebugTrace());
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
					e.printStackTrace();
				}

				System.out.println("----------------");
			}
		} while (line != null);
	}

}
