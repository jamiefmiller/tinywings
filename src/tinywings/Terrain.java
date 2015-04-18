package tinywings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Terrain {
	
	int[] heights = null;
	double[] slopes = null;
	Map<Integer, Region> regionMap = new HashMap<>();

	public Terrain(int raceLength) {
		// Define Terrain
		Random genRandom = new Random();
		List<Integer> wavelengths = new ArrayList<>();
		List<Double> amplitudes = new ArrayList<>();
		int sum = 0;
		int lengthMin = 60;
		int lengthMax = 100;
		double ampMin = -60.0;
		double ampMax = 60.0;
		double groundLevel = 200.0;
		while (sum < raceLength) {
			int length = genRandom.nextInt(lengthMax - lengthMin) + lengthMin;
			sum += length;
			wavelengths.add(length);
			amplitudes.add(genRandom.nextDouble()*(ampMax - ampMin) + ampMin + groundLevel);
		}
		wavelengths.add(genRandom.nextInt(lengthMax - lengthMin) + lengthMin);
		amplitudes.add(genRandom.nextDouble()*(ampMax - ampMin) + ampMin + groundLevel);
		
		// Create Terrain
		heights = new int[raceLength];
		slopes = new double[raceLength];
		int raceIndex = 0;
		for (int waveIndex = 0; waveIndex < wavelengths.size()-1; waveIndex++) {
			int length = wavelengths.get(waveIndex);
			int start = raceIndex;
			int end = start + length;
			double startHeight = amplitudes.get(waveIndex);
			double endHeight = amplitudes.get(waveIndex+1);
			double centerline = (startHeight + endHeight)/2;
			double amp = (startHeight-endHeight)/2;
			for (; raceIndex < raceLength && raceIndex < end; raceIndex++) {
				heights[raceIndex] = (int) (centerline+amp*Math.cos(Math.PI*(raceIndex-start)/length));
				slopes[raceIndex] = -Math.PI*(amp/length)*Math.sin(Math.PI*(raceIndex-start)/length);
				double completion = ((double) (raceIndex-start))/length;
				boolean ascending = startHeight < endHeight;
				// Assign a region
				Region r;
				if (completion < 0.2 && !ascending) {
					r = Region.HILLTOP;
				} else if (completion > 0.8 && ascending) {
					r = Region.HILLTOP;
				} else if (!ascending) {
					r = Region.DOWNHILL;
				} else {
					r = Region.UPHILL;
				}
				regionMap.put(raceIndex, r);
			}
		}
	}
	
	public int getHeight(int x) {
		return heights[x];
	}
	
	public double getSlope(int x) {
		return slopes[x];
	}
	
	public Region getRegion(int x) {
		return regionMap.get(x);
	}
}
