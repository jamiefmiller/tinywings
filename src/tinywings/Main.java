package tinywings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicStampedReference;

public class Main {
	private static final int width = 600;
	private static final int height = 400;
	private static final int raceLength = 2200;
	private static final int frameRate = 60;
	private static final int birdRadius = 10;
	
	public static void main(String[] args) {
		BufferedImage blank = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		AtomicStampedReference<BufferedImage> screen = new AtomicStampedReference<BufferedImage>(blank, 0);
		
		// Define Terrain
		Random terrainRandom = new Random();
		List<Integer> wavelengths = new ArrayList<>();
		List<Double> amplitudes = new ArrayList<>();
		int sum = 0;
		int lengthMin = 60;
		int lengthMax = 100;
		double ampMin = -60.0;
		double ampMax = 60.0;
		double groundLevel = 200.0;
		while (sum < raceLength) {
			int length = terrainRandom.nextInt(lengthMax - lengthMin) + lengthMin;
			sum += length;
			wavelengths.add(length);
			amplitudes.add(terrainRandom.nextDouble()*(ampMax - ampMin) + ampMin + groundLevel);
		}
		wavelengths.add(terrainRandom.nextInt(lengthMax - lengthMin) + lengthMin);
		amplitudes.add(terrainRandom.nextDouble()*(ampMax - ampMin) + ampMin + groundLevel);
		
		// Create Terrain
		Map<Integer, Region> regionMap = new HashMap<>();
		int[] heights = new int[raceLength];
		double[] slopes = new double[raceLength];
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
		
		// Create Birds
		List<Bird> birds = new ArrayList<>();
		birds.add(new PlayerBird(screen));
		Map<Bird, PhysicsBody> pMap = new HashMap<>();
		for (Bird bird : birds) {
			pMap.put(bird, new PhysicsBody(birdRadius+1, heights[birdRadius+1]+birdRadius, birdRadius));
		}
		Map<Bird, Color> colorMap = new HashMap<>();
		Color[] colors = new Color[4];
		colors[0] = Color.RED;
		colors[1] = Color.BLUE;
		colors[2] = Color.GREEN;
		colors[3] = Color.YELLOW;
		for (int i=0; i<birds.size(); i++) {
			colorMap.put(birds.get(i), colors[i]);
		}
		
		// Start Threads
		List<Thread> threads = new ArrayList<>();
		for (Bird bird : birds) {
			threads.add(new Thread(bird));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		
		// Run Race
		long nanoPerFrame = (1000*1000000)/frameRate;
		for (int frame=0; frame<raceLength; frame++) {
			long start = System.nanoTime();
			// Update bird positions. Done at start of loop to give birds a
			// chance to respond during the idle wait between frames.
			for (Bird bird : birds) {
				PhysicsBody p = pMap.get(bird);
				if (bird.getPressed()) {
					p.press();
				} else {
					p.unpress();
				}
			}
			
			// Generate screen
			int offset = Math.min(frame, raceLength-width);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Color land = new Color(90, 210, 60);
			Color sky = new Color(180, 210, 255);
			Graphics g = image.createGraphics();
			g.setColor(sky);
			g.fillRect(0, 0, width, height);
			g.setColor(land);
			for (int x=0; x<width; x++) {
				g.drawLine(x, height, x, height-heights[x]);//+offset]);
			}
			
			// Draw birds on screen
			for (Bird bird : birds) {
				PhysicsBody p = pMap.get(bird);
				//int dheight = heights[p.getX()] - heights[p.getX()+1];
				p.update(heights[p.getX()], slopes[p.getX()], regionMap.get(p.getX()));
				Color c = colorMap.get(bird);
				g.setColor(c);
				int x = p.getX();
				int y = p.getY();
				int r = p.getR();
				g.fillOval(x-r, height-y-r, 2*r, 2*r);
			}
			
			// Send screen to birds
			screen.set(image, frame);
			
			// Idle wait to keep constant frame rate
			while (System.nanoTime()-start < nanoPerFrame){}
		}
		
		// End Program
		screen.set(blank, -1); // Stamp of -1 is shutdown signal.
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {}
		}
	}

}
