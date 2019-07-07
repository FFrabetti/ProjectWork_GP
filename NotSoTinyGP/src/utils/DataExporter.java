package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataExporter {

	private static final String DEF_SEPARATOR = ";";

	public static <T> void createCSV(T[] pop, String fileName, Function<T,String[]> nodeToValues, String separator, String[] header) {
		String[][] lines = new String[pop.length][];
		int i = 0;
		for(T n : pop)
			lines[i++] = nodeToValues.apply(n);
		
		createCSV(lines, fileName, separator, header);
	}

	public static <T> void createCSV(T[] pop, String fileName, Function<T,String[]> nodeToValues, String separator) {
		createCSV(pop, fileName, nodeToValues, separator, null);
	}

	public static <T> void createCSV(T[] pop, String fileName, Function<T,String[]> nodeToValues) {
		createCSV(pop, fileName, nodeToValues, DEF_SEPARATOR);
	}

	public static <T> void createCSV(T[] pop, String fileName, Function<T,String[]> nodeToValues, String[] header) {
		createCSV(pop, fileName, nodeToValues, DEF_SEPARATOR, header);
	}

	public static void createCSV(String[][] lines, String fileName, String separator, String[] header) {
		try(PrintWriter pw = new PrintWriter(new File(fileName))) {
			Collector<CharSequence, ?, String> collector = Collectors.joining(separator);
			
			if(header != null)
				pw.println(Stream.of(header).collect(collector));
			
			for(String[] line : lines)
				pw.println(Stream.of(line).collect(collector));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println(DataExporter.class.getSimpleName() + ": created file \" " + fileName + " \" ");
	}
	
	public static void createCSV(String[][] lines, String fileName, String[] header) {
		createCSV(lines, fileName, DEF_SEPARATOR, header);
	}
	
}
