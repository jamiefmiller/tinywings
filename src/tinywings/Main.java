package tinywings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicStampedReference;

public class Main {
	private static final int width = 600;
	private static final int height = 400;
	private static final int raceLength = 2200;
	private static final int frameRate = 60;
	private static final Color landColor = new Color(90, 210, 60);
	private static final Color skyColor = new Color(180, 210, 255);
	
	private static List<BirdTracker> birds = new ArrayList<>();
	
	public static void main(String[] args) {
		BufferedImage blank = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		AtomicStampedReference<BufferedImage> screen = new AtomicStampedReference<BufferedImage>(blank, 0);
		
		Terrain terrain = new Terrain(raceLength);
		
		// Create Birds
		Color[] colors = new Color[4];
		colors[0] = Color.RED;
		colors[1] = Color.BLUE;
		colors[2] = Color.GREEN;
		colors[3] = Color.YELLOW;
		birds.add(new BirdTracker(colors[0], true, blank, terrain));
		
		// Start Threads
		List<Thread> threads = new ArrayList<>();
		for (BirdTracker bird : birds) {
			threads.add(bird.createThread());
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
			for (BirdTracker bird : birds) {
				bird.update();
			}
			
			// Draw a screen for each bird.
			Map<BirdTracker, BufferedImage> screenBuffer = new HashMap<>();
			for (BirdTracker bird : birds) {
				BufferedImage image = createScreenForBird(terrain, bird);
				screenBuffer.put(bird, image);
			}
			
			// Send screen to birds.
			for (BirdTracker bird : birds) {
				BufferedImage image = screenBuffer.get(bird);
				screen.set(image, frame);
			}
			
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

	private static BufferedImage createScreenForBird(Terrain terrain, BirdTracker bird) {
		// Generate screen
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		g.setColor(skyColor);
		g.fillRect(0, 0, width, height);
		g.setColor(landColor);
		// Draw the scene with the bird 1/3 from the left, and avoid drawing off-screen areas.
		int offset = Math.min(Math.max(bird.getX() - width/3, 0), raceLength-width);
		for (int x=0; x<width; x++) {
			g.drawLine(x, height, x, height-terrain.getHeight(x+offset));
		}
		
		// If it's a player, draw all birds.
		if (bird.isPlayer()) {
			for (BirdTracker otherBird : birds) {
				if (otherBird != bird) {
					drawBirdWithOffset(g, otherBird, offset);
				}
			}
		}
		
		// Draw the viewing bird.
		drawBirdWithOffset(g, bird, offset);
		
		g.dispose();
		return image;
	}
	
	private static void drawBirdWithOffset(Graphics g, BirdTracker bird, int offset) {
		Color c = bird.getColor();
		g.setColor(c);
		int x = bird.getX();
		int y = bird.getY();
		int r = bird.getR();
		g.fillOval(x-r-offset, height-y-r, 2*r, 2*r);
	}
}
