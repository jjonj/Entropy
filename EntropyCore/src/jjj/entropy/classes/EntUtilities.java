package jjj.entropy.classes;

import java.util.Date;
import java.util.Random;

//Like all statically used classes, they are shared across the VM. So the server & client or two of one should not share VM!

public class EntUtilities {

	private static long randomSeed1, randomSeed2;
	
	private static boolean isSeeded;
	
	private static Random rand1, rand2;
	
	
	public static int GetRandom(int seedSource, int min, int max)
	{
		if (!isSeeded)
		{
			randomSeed1 = new Date().getTime();
			randomSeed2 = randomSeed1 + 1000;	//Is this proper?
			rand1 = new Random(randomSeed1); 
			rand2 = new Random(randomSeed2); 
			isSeeded = true;
		}
		if (seedSource == 1)
			return min + (int)(rand1.nextDouble() * ((max - min) + 1));
		return min + (int)(rand2.nextDouble() * ((max - min) + 1));
	}
	
	public static long GetSeed(int which)
	{
		if (which == 1)
			return randomSeed1;
		return randomSeed2;
	}
	
	public static void SetSeed(long seed1, long seed2)
	{
		randomSeed1 = seed1;
		randomSeed2 = seed2;
		rand1 = new Random(randomSeed1);
		rand2 = new Random(randomSeed2);
		isSeeded = true;
	}

	public static Random GetRndObject(int seedHost) {
		if (seedHost == 1)
			return rand1;
		return rand2;
	}

}
