package jjj.entropy;

import java.util.Date;
import java.util.Random;

public class EntUtilities {

	private static long randomSeed;
	
	private static boolean isSeeded;
	
	private static Random rand;
	
	
	public static int GetRandom(int min, int max)
	{
		if (!isSeeded)
		{
			randomSeed = new Date().getTime();
			rand = new Random(randomSeed); 
			isSeeded = true;
		}
		return min + (int)(rand.nextDouble() * ((max - min) + 1));
	}
	
	public static long GetSeed()
	{
		return randomSeed;
	}
	
	public static void SetSeed(long seed)
	{
		randomSeed = seed;
		rand = new Random(randomSeed);
		isSeeded = true;
		
	}
}
