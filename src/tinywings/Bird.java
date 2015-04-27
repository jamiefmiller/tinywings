package tinywings;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicStampedReference;

public abstract class Bird implements Runnable {
	protected AtomicBoolean pressed;
	protected AtomicStampedReference<BufferedImage> screen;
	protected List<AtomicStampedReference<BufferedImage>> screens;

	public Bird(AtomicStampedReference<BufferedImage> screen, List<AtomicStampedReference<BufferedImage>> screens) {
		this.pressed = new AtomicBoolean(false);
		this.screen = screen;
		this.screens = screens;
	}
	
	public Boolean getPressed() {
		return pressed.get();
	}
	
	abstract boolean computeFrame();
}
