package scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexScanner implements Scanner {
	
	private static final Pattern pattern = Pattern.compile(
			ExpToken.numberRegex + 
			"|[-+*/%)(^=$,]|" + 
			ExpToken.identRegex
	);
	
	private Matcher matcher;
	private int i;
	
	public RegexScanner() {
		
	}
	
	public RegexScanner(String str) {
		readLine(str);
	}

	@Override
	public String nextToken() {
		if(matcher.find(i)) {
			i = matcher.end();
			return matcher.group(0); // group #0 is the entire RE
		}
		else
			return null;
	}

	@Override
	public boolean hasMoreTokens() {
		return matcher != null && matcher.find(i);
	}

	@Override
	public void readLine(String line) {
        matcher = pattern.matcher(line);
        i = 0;
	}

}
