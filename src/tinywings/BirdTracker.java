package tinywings;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicStampedReference;

public class BirdTracker {
	
	private static final int radius = 10;
	
	private Bird bird;
	private AtomicStampedReference<BufferedImage> screen;
	private Color color;
	private Terrain terrain;
	private PhysicsBody body;
	private boolean isPlayer;

	public BirdTracker(Color color, boolean isPlayer, BufferedImage startScreen, Terrain terrain) {
		this.color = color;
		this.terrain = terrain;
		this.isPlayer = isPlayer;
		body = new PhysicsBody(radius+1, terrain.getHeight(radius+1)+radius, radius);
		screen = new AtomicStampedReference<BufferedImage>(startScreen, 0);
		if (isPlayer) {
			this.bird = new PlayerBird(screen);
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
	
	public void update() {
		if (bird.getPressed()) {
			body.press();
		} else {
			body.unpress();
		}
		int x = body.getX();
		body.update(terrain.getHeight(x), terrain.getSlope(x), terrain.getRegion(x));
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
