package scanner;

import java.util.StringTokenizer;

public class BlankScanner implements Scanner {
	
	private StringTokenizer strTkn;
	
	public BlankScanner() {
		
	}
	
	public BlankScanner(String str) {
		readLine(str);
	}

	@Override
	public String nextToken() {
		return strTkn.nextToken();
	}

	@Override
	public boolean hasMoreTokens() {
		return strTkn != null && strTkn.hasMoreTokens();
	}

	@Override
	public void readLine(String line) {
		strTkn = new StringTokenizer(line);
	}
	
}
