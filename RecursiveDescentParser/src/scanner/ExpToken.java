package scanner;

import java.util.Optional;
import java.util.regex.Pattern;

public class ExpToken {

	public static final String intRegex = "0|[1-9][0-9]*";
	public static final String identRegex = "[a-zA-Z][0-9a-zA-Z]*";
	public static final String numberRegex = intRegex + "(\\.[0-9]+)?";
	
	private static final Pattern numberPattern = Pattern.compile(numberRegex);
	private static final Pattern identPattern = Pattern.compile(identRegex);
	
	private Optional<String> value;
	
	public ExpToken(String value) {
		this.value = Optional.ofNullable(value);
	}
	
	public String getValue() {
		return value.get();
	}
	
	public boolean isPresent() {
		return value.isPresent();
	}
	
	public boolean isNumber() {
		return value.isPresent() && numberPattern.matcher(value.get()).matches();
	}
	
	public boolean isIdentifier() {
		return value.isPresent() && identPattern.matcher(value.get()).matches();
	}
	
	public boolean isString(String s) {
		return value.isPresent() && value.get().equals(s);
	}
	
	@Override
	public String toString() {
		return value.isPresent() ? value.get() : "EMPTY_TOKEN";
	}
	
}
