package tinywings;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class SleepyBird extends Bird {
	
	private AtomicInteger framesmissed;
	private int previousframe = 0;
	//private boolean lookedForBetterstampOnce = false;

	public SleepyBird(AtomicStampedReference<BufferedImage> screen, List<AtomicStampedReference<BufferedImage>> screens, AtomicInteger framesmissed) {
		super(screen, screens);
		this.framesmissed = framesmissed;
	}
	
	@Override
	public void run() {
		while(computeFrame()) {}
	}

	@Override
	boolean computeFrame() {
		int[] stamp = new int[1];
		stamp[0] = 0;
		screen.get(stamp);
		if (stamp[0] < 0){
			return false;
		}
		/*for (AtomicStampedReference<BufferedImage> otherscreen : screens) {
			while(otherscreen.getStamp() < stamp[0] && otherscreen.getStamp() > 0){}
			if (otherscreen.getStamp() > stamp[0] && !lookedForBetterstampOnce) {
				lookedForBetterstampOnce = true;
				return true;
			}
		}*/
		if (stamp[0] > previousframe+1) {
			framesmissed.addAndGet(stamp[0]-(previousframe+1));
		}
		previousframe = stamp[0];
		try {
			Thread.sleep(18);
		} catch (InterruptedException e) {}
		return true;
	}
}
