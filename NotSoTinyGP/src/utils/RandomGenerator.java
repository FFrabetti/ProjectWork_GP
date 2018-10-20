package utils;

import java.util.Random;

public class RandomGenerator {

	private static RandomGenerator instance;
	
	public static RandomGenerator getInstance() {
		if(instance == null)
			instance = new RandomGenerator();
		
		return instance;
	}
	
	private Random rnd;
	
	private RandomGenerator() {
		rnd = new Random();
	}
	
	public void setSeed(long seed) {
		rnd.setSeed(seed);
	}
	
	public int nextInt() {
		return rnd.nextInt();
	}
	
	public int nextInt(int bound) {
		return rnd.nextInt(bound);
	}

	public double nextDouble() {
		return rnd.nextDouble();
	}
	
}
