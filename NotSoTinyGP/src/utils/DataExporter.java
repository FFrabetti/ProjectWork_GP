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

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> funct, String separator, String[] header) {
		try(PrintWriter pw = new PrintWriter(new File(fileName))) {
			Collector<CharSequence, ?, String> collector = Collectors.joining(separator);
			
			if(header != null)
				pw.println(Stream.of(header).collect(collector));
			
			for(Node n : pop)
				pw.println(Stream.of(funct.apply(n)).collect(collector));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("Created file: " + fileName);
	}

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> funct, String separator) {
		createCSV(pop, fileName, funct, separator, null);
	}

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> funct) {
		createCSV(pop, fileName, funct, ";");
	}

	public static void createCSV(Node[] pop, String fileName, Function<Node,String[]> funct, String[] header) {
		createCSV(pop, fileName, funct, ";", header);
	}

}
