package tinygp;

/* 
 * Program:   tiny_gp.java
 *
 * Author:    Riccardo Poli (email: rpoli@essex.ac.uk)
 *
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.function.Function;

import utils.DataExporter;

public class TinyGP {
	double[] fitness;
	char[][] pop;
	private char[] currBest;
	static Random rd = new Random();
	static final int ADD = 110, SUB = 111, MUL = 112, DIV = 113, FSET_START = ADD, FSET_END = DIV;
	static double[] x = new double[FSET_START];
	static double minrandom, maxrandom;
	static char[] program;
	static int PC;
	static int varnumber, fitnesscases, randomnumber;
	static double fbestpop = 0.0, favgpop = 0.0;
	static long seed;
	static double avg_len;
	static int MAX_LEN = 10000;
	static int POPSIZE = 100000;
	static int DEPTH = 5;
	static int GENERATIONS = 100;
	static int TSIZE = 2;
	public static double PMUT_PER_NODE = 0.05;
	public static double CROSSOVER_PROB = 0.9;
	static double FITNESS_DELTA = -1e-5;
	static double[][] targets;

	void setParameters() {
		// assign values to static variables
	}
	
	double run() { /* Interpreter */
		char primitive = program[PC++];
		if (primitive < FSET_START)
			return (x[primitive]);
		switch (primitive) {
		case ADD:
			return (run() + run());
		case SUB:
			return (run() - run());
		case MUL:
			return (run() * run());
		case DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		}
		return (0.0); // should never get here
	}

	int traverse(char[] buffer, int buffercount) {
		if (buffer[buffercount] < FSET_START)
			return (++buffercount);

		switch (buffer[buffercount]) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		}
		return (0); // should never get here
	}

	void setup_fitness(String fname) {
		try {
			int i, j;
			String line;

			BufferedReader in = new BufferedReader(new FileReader(fname));
			line = in.readLine();
			StringTokenizer tokens = new StringTokenizer(line);
			varnumber = Integer.parseInt(tokens.nextToken().trim());
			randomnumber = Integer.parseInt(tokens.nextToken().trim());
			minrandom = Double.parseDouble(tokens.nextToken().trim());
			maxrandom = Double.parseDouble(tokens.nextToken().trim());
			fitnesscases = Integer.parseInt(tokens.nextToken().trim());
			targets = new double[fitnesscases][varnumber + 1];
			if (varnumber + randomnumber >= FSET_START)
				System.out.println("too many variables and constants");

			for (i = 0; i < fitnesscases; i++) {
				line = in.readLine();
				tokens = new StringTokenizer(line);
				for (j = 0; j <= varnumber; j++) {
					targets[i][j] = Double.parseDouble(tokens.nextToken().trim());
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Please provide a data file");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("ERROR: Incorrect data format");
			e.printStackTrace();
			System.exit(0);
		}
	}

	double fitness_function(char[] Prog) {
		int i = 0, len;
		double result, fit = 0.0;

		len = traverse(Prog, 0);
		for (i = 0; i < fitnesscases; i++) {
			for (int j = 0; j < varnumber; j++)
				x[j] = targets[i][j];
			program = Prog;
			PC = 0;
			result = run();
			fit += Math.abs(result - targets[i][varnumber]);
		}
		return (-fit);
	}

	int grow(char[] buffer, int pos, int max, int depth) {
		char prim = (char) rd.nextInt(2);
		int one_child;

		if (pos >= max)
			return (-1);

		if (pos == 0)
			prim = 1;

		if (prim == 0 || depth == 0) {
			prim = (char) rd.nextInt(varnumber + randomnumber);
			buffer[pos] = prim;
			return (pos + 1);
		} else {
			prim = (char) (rd.nextInt(FSET_END - FSET_START + 1) + FSET_START);
			switch (prim) {
			case ADD:
			case SUB:
			case MUL:
			case DIV:
				buffer[pos] = prim;
				one_child = grow(buffer, pos + 1, max, depth - 1);
				if (one_child < 0)
					return (-1);
				return (grow(buffer, one_child, max, depth - 1));
			}
		}
		return (0); // should never get here
	}

	int print_indiv(char[] buffer, int buffercounter) {
		StringBuilder sb = new StringBuilder();
		int result = print_indiv(buffer, buffercounter, sb);
		System.out.println(sb.toString());
		
		return result;
	}

	private int print_indiv(char[] buffer, int buffercounter, StringBuilder sb) {
		int a1 = 0, a2;
		if (buffer[buffercounter] < FSET_START) {
			if (buffer[buffercounter] < varnumber)
				sb.append("X" + (buffer[buffercounter] + 1) + " ");
			else
				sb.append(x[buffer[buffercounter]]);
			return (++buffercounter);
		}
		switch (buffer[buffercounter]) {
		case ADD:
			sb.append("(");
			a1 = print_indiv(buffer, ++buffercounter, sb);
			sb.append(" + ");
			break;
		case SUB:
			sb.append("(");
			a1 = print_indiv(buffer, ++buffercounter, sb);
			sb.append(" - ");
			break;
		case MUL:
			sb.append("(");
			a1 = print_indiv(buffer, ++buffercounter, sb);
			sb.append(" * ");
			break;
		case DIV:
			sb.append("(");
			a1 = print_indiv(buffer, ++buffercounter, sb);
			sb.append(" / ");
			break;
		}
		a2 = print_indiv(buffer, a1, sb);
		sb.append(")");
		return (a2);		
	}
	
	static char[] buffer = new char[MAX_LEN];

	char[] create_random_indiv(int depth) {
		char[] ind;
		int len;

		len = grow(buffer, 0, MAX_LEN, depth);

		while (len < 0)
			len = grow(buffer, 0, MAX_LEN, depth);

		ind = new char[len];

		System.arraycopy(buffer, 0, ind, 0, len);
		return (ind);
	}

	char[][] create_random_pop(int n, int depth, double[] fitness) {
		char[][] pop = new char[n][];
		int i;

		for (i = 0; i < n; i++) {
			pop[i] = create_random_indiv(depth);
			fitness[i] = fitness_function(pop[i]);
		}
		return (pop);
	}

	String[] stats(double[] fitness, char[][] pop, int gen) {
		int i, best = rd.nextInt(POPSIZE);
		int node_count = 0;
		fbestpop = fitness[best];
		favgpop = 0.0;
		int minsize = Integer.MAX_VALUE;
		int maxsize = -1;
		int size;
		
		for (i = 0; i < POPSIZE; i++) {
			size = traverse(pop[i], 0);
			if(size < minsize)
				minsize = size;
			if(size > maxsize)
				maxsize = size;
			
			node_count += size;
			favgpop += fitness[i];
			if (fitness[i] > fbestpop) {
				best = i;
				fbestpop = fitness[i];
			}
		}
		
		currBest = pop[best];
		avg_len = (double) node_count / POPSIZE;
		favgpop /= POPSIZE;
		System.out.print("Generation=" + gen + " Avg Fitness=" + (-favgpop) + " Best Fitness=" + (-fbestpop)
				+ " Avg Size=" + avg_len + "\nBest Individual: ");
//		print_indiv(pop[best], 0);
		System.out.print("\n");
		System.out.flush();
		
		//generation;minDepth;maxDepth;avDepth;minSize;maxSize;avSize;maxFitness;avFitness
		return new String[] {
				String.valueOf(gen), "-1", "-1", "-1",
				String.valueOf(minsize), String.valueOf(maxsize), String.valueOf(avg_len),
				String.valueOf((-fbestpop)), String.valueOf((-favgpop))
		};
	}

	int tournament(double[] fitness, int tsize) {
		int best = rd.nextInt(POPSIZE), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(POPSIZE);
			if (fitness[competitor] > fbest) {
				fbest = fitness[competitor];
				best = competitor;
			}
		}
		return (best);
	}

	int negative_tournament(double[] fitness, int tsize) {
		int worst = rd.nextInt(POPSIZE), i, competitor;
		double fworst = 1e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(POPSIZE);
			if (fitness[competitor] < fworst) {
				fworst = fitness[competitor];
				worst = competitor;
			}
		}
		return (worst);
	}

	char[] crossover(char[] parent1, char[] parent2) {
		int xo1start, xo1end, xo2start, xo2end;
		char[] offspring;
		int len1 = traverse(parent1, 0);
		int len2 = traverse(parent2, 0);
		int lenoff;

		xo1start = rd.nextInt(len1);
		xo1end = traverse(parent1, xo1start);

		xo2start = rd.nextInt(len2);
		xo2end = traverse(parent2, xo2start);

		lenoff = xo1start + (xo2end - xo2start) + (len1 - xo1end);

		offspring = new char[lenoff];

		System.arraycopy(parent1, 0, offspring, 0, xo1start);
		System.arraycopy(parent2, xo2start, offspring, xo1start, (xo2end - xo2start));
		System.arraycopy(parent1, xo1end, offspring, xo1start + (xo2end - xo2start), (len1 - xo1end));

		return (offspring);
	}

	char[] mutation(char[] parent, double pmut) {
		int len = traverse(parent, 0), i;
		int mutsite;
		char[] parentcopy = new char[len];

		System.arraycopy(parent, 0, parentcopy, 0, len);
		for (i = 0; i < len; i++) {
			if (rd.nextDouble() < pmut) {
				mutsite = i;
				if (parentcopy[mutsite] < FSET_START)
					parentcopy[mutsite] = (char) rd.nextInt(varnumber + randomnumber);
				else
					switch (parentcopy[mutsite]) {
					case ADD:
					case SUB:
					case MUL:
					case DIV:
						parentcopy[mutsite] = (char) (rd.nextInt(FSET_END - FSET_START + 1) + FSET_START);
					}
			}
		}
		return (parentcopy);
	}

	void print_parms() {
		System.out.print("-- TINY GP (Java version) --\n");
		System.out.print("SEED=" + seed + "\nMAX_LEN=" + MAX_LEN + "\nPOPSIZE=" + POPSIZE + "\nDEPTH=" + DEPTH
				+ "\nCROSSOVER_PROB=" + CROSSOVER_PROB + "\nPMUT_PER_NODE=" + PMUT_PER_NODE + "\nMIN_RANDOM="
				+ minrandom + "\nMAX_RANDOM=" + maxrandom + "\nGENERATIONS=" + GENERATIONS + "\nTSIZE=" + TSIZE
				+ "\n----------------------------------\n");
	}

	public TinyGP(String fname, long s) {
		// FIFR:
		setParameters();
		
		fitness = new double[POPSIZE];
		seed = s;
		if (seed >= 0)
			rd.setSeed(seed);
		setup_fitness(fname);
		for (int i = 0; i < FSET_START; i++)
			x[i] = (maxrandom - minrandom) * rd.nextDouble() + minrandom;
		pop = create_random_pop(POPSIZE, DEPTH, fitness);
	}

	void evolve() {
		int gen = 0, indivs, offspring, parent1, parent2, parent;
		double newfit;
		char[] newind;
		print_parms();
		// ---------------------------------------------------
		String[] line = stats(fitness, pop, 0);
		createCSV(pop, "initial");
		List<String[]> list = new LinkedList<>();
		list.add(line);
		// ---------------------------------------------------
		boolean success = false;
		for (gen = 1; !success && gen < GENERATIONS; gen++) {			
			if (fbestpop > FITNESS_DELTA) {
				System.out.print("PROBLEM SOLVED\n");
//				System.exit(0);
				success = true;
			}
			else {
				for (indivs = 0; indivs < POPSIZE; indivs++) {
					if (rd.nextDouble() < CROSSOVER_PROB) {
						parent1 = tournament(fitness, TSIZE);
						parent2 = tournament(fitness, TSIZE);
						newind = crossover(pop[parent1], pop[parent2]);
					} else {
						parent = tournament(fitness, TSIZE);
						newind = mutation(pop[parent], PMUT_PER_NODE);
					}
					newfit = fitness_function(newind);
					offspring = negative_tournament(fitness, TSIZE);
					pop[offspring] = newind;
					fitness[offspring] = newfit;
				}
				// ---------------------------------------------------
				list.add(stats(fitness, pop, gen));
				// ---------------------------------------------------
			}
		}
//		System.out.print("PROBLEM *NOT* SOLVED\n");
		// ---------------------------------------------------
		createCSV(pop, "final");
		createResultFile(gen-1, success, currBest, fbestpop, "result");
		createSummaryCSV(list, "summary");
		// ---------------------------------------------------
//		System.exit(1);
	}

	void createSummaryCSV(List<String[]> list, String name) {
		String[][] lines = new String[list.size()][];
		int i = 0;
		for(String[] line : list)
			lines[i++] = line;
		
		DataExporter.createCSV(lines, name + ".csv", new String[] {"generation",
				"minDepth", "maxDepth", "avDepth", "minSize", "maxSize", "avSize", "maxFitness", "avFitness"});
	}
	
	void createCSV(char[][] pop, String name) {
		Function<char[], String[]> funct = n -> { // for each node
			return new String[] {
					String.valueOf(-1),
					String.valueOf(traverse(n, 0)),
					String.valueOf(fitness_function(n))};
		};

		String[] labels = new String[] {"depth","size","fitness"};
		
		DataExporter.createCSV(pop, name + ".csv", funct, labels);
		// REMEMBER to set an "English locale" when you open them (decimal separator . )
	}

	void createResultFile(int gen, boolean success, char[] best, double fitness, String name) {
		try(PrintWriter pw = new PrintWriter(new FileWriter(name + ".result"))) {
			pw.println("generation = " + gen + " - " + (success ? "SUCCESS" : "FAIL"));
			pw.println("best fitness = " + fitness);
			
			StringBuilder sb = new StringBuilder();
			print_indiv(best, 0, sb);
			pw.println("best node = " + sb.toString());
			
			System.out.println("Created file: " + name + ".result");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String fname = "problem.dat";
		long s = -1;

		if (args.length == 2) {
			// FIFR: swapped the 2 args
			s = Integer.valueOf(args[1]).intValue();
			fname = args[0];
		}
		if (args.length == 1) {
			fname = args[0];
		}

		TinyGP gp = new TinyGP(fname, s);
		gp.evolve();
	}
};
