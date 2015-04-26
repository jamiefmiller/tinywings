package tinywings;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class BirdTracker {
	
	private static final int radius = 10;
	
	private Bird bird;
	private AtomicStampedReference<BufferedImage> screen;
	private Color color;
	private Terrain terrain;
	private PhysicsBody body;
	private boolean isPlayer;
	private AtomicInteger framesMissed;

	public BirdTracker(Color color, boolean isPlayer, BufferedImage startScreen, Terrain terrain) {
		this.color = color;
		this.terrain = terrain;
		this.isPlayer = isPlayer;
		body = new PhysicsBody(radius+1, terrain.getHeight(radius+1)+radius, radius);
		framesMissed = new AtomicInteger(0);
		screen = new AtomicStampedReference<BufferedImage>(startScreen, 0);
		if (isPlayer) {
			this.bird = new PlayerBird(screen);
		} else {
			this.bird = new AIbird(screen, color, radius, 0, framesMissed);
		}
	}
	
	public Thread createThread() {
		return new Thread(bird);
	}
	
	public void setScreen(BufferedImage image, int frame) {
		screen.set(image, frame);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void update(int fps) {
		if (bird.getPressed()) {
			body.press();
		} else {
			body.unpress();
		}
		int x = body.getX();
		double scale = 60.0/(double) fps;
		body.update(terrain.getHeight(x), terrain.getSlope(x), terrain.getRegion(x), scale);
	}
	
	public int getX() {
		return body.getX();
	}
	
	public int getY() {
		return body.getY();
	}
	
	public int getR() {
		return radius;
	}
	
	public boolean isPlayer() {
		return isPlayer;
	}
}
