package tinywings;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicStampedReference;

public abstract class Bird implements Runnable {
	protected AtomicBoolean pressed;
	protected AtomicStampedReference<BufferedImage> screen;

	public Bird(AtomicStampedReference<BufferedImage> screen) {
		this.pressed = new AtomicBoolean(false);
		this.screen = screen;
	}
	
	public Boolean getPressed() {
		return pressed.get();
	}
}
