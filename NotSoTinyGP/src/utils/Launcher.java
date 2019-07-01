package utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Stream;

public class Launcher implements Launchable {

	public static void main(String[] args) {
		// test main: this is how to launch NotSoTinyGP in order to have an homogeneous
		// command line interface
		Launcher.launch(args, new Launcher());

	}

	// args: [configFile] [seed] [propertiesFile]
	public static void launch(String[] args, Launchable launchable) {
		String fname = "problem.dat";
		long s = -1;
		String propertiesFile = null;

		switch (args.length) { // using the cascade effect
		case 3:
			propertiesFile = args[2];
		case 2:
			s = Integer.valueOf(args[1]).intValue();
		case 1:
			fname = args[0];
		default:
			if (propertiesFile == null)
				propertiesFile = searchPropertiesFile();
		}

		Random random = new Random();
		if (s >= 0)
			random.setSeed(s);

		Properties properties = new Properties();
		properties.put("configFile", fname);
		properties.put("seed", s);
		properties.put("random", random);

		// good practice: use defaults if not present
		try (FileReader reader = new FileReader(propertiesFile)) {
			properties.load(reader);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
		}

		// print args
		System.out.println(launchable.getClass().getSimpleName() + ": " + fname + ", " + s + ", " + propertiesFile);
		
		launchable.launch(properties);
	}

	private static String searchPropertiesFile() {
		Optional<File> file = Stream.of(new File(".").listFiles(f -> f.getName().endsWith(".properties") && f.isFile()))
				.findFirst();

		return file.isPresent() ? file.get().getName() : null;
	}

	@Override
	// just for testing
	public void launch(Properties properties) {
		// properties.list(System.out);
		properties.forEach((k, v) -> System.out.println(k + ": " + v));

		// try {
		// properties.store(new FileWriter("test.properties"), "test comment");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public static double getDouble(Properties p, String key, double defaultVal) {
		Object o = p.get(key);
		double result = defaultVal;

		try {
			if (o instanceof Double)
				result = ((Double) o).doubleValue();
			else if (o instanceof String)
				result = Double.parseDouble((String) o);
		} catch (Exception e) {
			// do nothing
		}

		return result;
	}
	
	public static int getInt(Properties p, String key, int defaultVal) {
		Object o = p.get(key);
		int result = defaultVal;

		try {
			if (o instanceof Integer)
				result = ((Integer) o).intValue();
			else if (o instanceof String)
				result = Integer.parseInt((String) o);
		} catch (Exception e) {
			// do nothing
		}

		return result;
	}

}
