package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Node;

public class DataExporter {

	private static final String DEF_SEPARATOR = ";";

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> nodeToValues, String separator, String[] header) {
		try(PrintWriter pw = new PrintWriter(new File(fileName))) {
			Collector<CharSequence, ?, String> collector = Collectors.joining(separator);
			
			if(header != null)
				pw.println(Stream.of(header).collect(collector));
			
			for(Node n : pop)
				pw.println(Stream.of(nodeToValues.apply(n)).collect(collector));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println(DataExporter.class.getSimpleName() + ": created file \" " + fileName + " \" ");
	}

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> nodeToValues, String separator) {
		createCSV(pop, fileName, nodeToValues, separator, null);
	}

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> nodeToValues) {
		createCSV(pop, fileName, nodeToValues, DEF_SEPARATOR);
	}

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> nodeToValues, String[] header) {
		createCSV(pop, fileName, nodeToValues, DEF_SEPARATOR, header);
	}

}
