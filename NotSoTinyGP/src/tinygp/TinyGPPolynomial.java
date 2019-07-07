package tinygp;

import java.util.List;

// -------------- polynomial.properties --------------
//generations=100
//popsize=500
//pTerm=0.5
//tsize=8
//pmut_per_node=0.05
//mindepth=1
//seed=2
//fitness_delta=0.05
//maxdepth=3
//max_len=10000
//crossover_prob=0.9
//configFile=resources/polynomial-data.txt
//mutation_prob=0.08

public class TinyGPPolynomial extends TinyGP {

	private static long SEED = 2;
	private static String CONFIG_FILE = "resources/polynomial-data_tinygp.txt";;
	private static String PATH;
	
	public TinyGPPolynomial(String fname, long s) {
		super(fname, s);
		
		long timestamp = System.currentTimeMillis();
		PATH = "runs/" + TinyGPPolynomial.class.getSimpleName() + "_" + timestamp;
	}

	@Override
	void setParameters() {
		GENERATIONS = 100;
		POPSIZE = 500; //100000;
		//pTerm not parametric, but set to 0.5
		TSIZE = 8;
		PMUT_PER_NODE = 0.05;
		//seed = 2;
		FITNESS_DELTA = -0.05;
		DEPTH = 3;
		MAX_LEN = 10000; // not used in NSTGP
		CROSSOVER_PROB = 0.9;
	}
	
	@Override
	void createResultFile(int gen, boolean success, char[] best, double fitness, String name) {
		super.createResultFile(gen, success, best, fitness, PATH);
	}

	@Override
	void createCSV(char[][] pop, String name) {
		super.createCSV(pop, PATH + "_" + name);
	}
	
	@Override
	void createSummaryCSV(List<String[]> list, String name) {
		super.createSummaryCSV(list, PATH + "_summary");
	}
	
	public static void main(String[] args) {
		TinyGP gp = new TinyGPPolynomial(CONFIG_FILE, SEED);
		gp.evolve();
	}
}
