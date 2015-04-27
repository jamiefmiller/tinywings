package tinywings;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class AIbird extends Bird {
	private int previousframe = 0;
	private AtomicInteger framesmissed = null;
	private int[] y;
	private int birdx;
	private int birdy;
	private double speedx;
	private double speedy;
	private int radius;
	private double g = 0.08;
	private int skill;
	private Color pix;
	private Random r;
	private Color birdcolor;
	
	private double distribution = 0;
	private double landslope;
	private double landx;
	private double landy;
	private int missed;
	private int movedx;
	private int previousy;
	private int previousx;
	private double time;
	private int previousheight = 0;
	private double slope;
	
	//private boolean lookedForBetterstampOnce = false;

	public AIbird(AtomicStampedReference<BufferedImage> screen, List<AtomicStampedReference<BufferedImage>> screens, Color birdcolor, int radius, int skill, AtomicInteger framesmissed) {
		super(screen, screens);
		this.birdcolor = birdcolor;
		r = new Random();
		this.radius = radius;
		this.skill = skill;
		this.framesmissed = framesmissed;
		y = new int[screen.getReference().getWidth()];
	}

	@Override
	public void run() {
		while(computeFrame()) {}
	}

	@Override
	boolean computeFrame() {
		int[] stamp = new int[1];
		stamp[0] = 0;
		BufferedImage image = screen.get(stamp);
		if (stamp[0] < 0){
			return false;
		}
		//for (AtomicStampedReference<BufferedImage> otherscreen : screens) {
			//while(otherscreen.getStamp() < stamp[0] && otherscreen.getStamp() > 0){}
			/*if (otherscreen.getStamp() > stamp[0] && !lookedForBetterstampOnce) {
				lookedForBetterstampOnce = true;
				return true;
			}*/
		//}
		if (stamp[0] > previousframe) {
			//long starttime = System.nanoTime();
			//System.out.println(pressed.get());
			previousy = birdy;
			previousx = birdx;
			// Finds the line
			for(int i = 0; i<image.getWidth(); i++){
				int findy = 0;
				pix = null;
				while(!Main.landColor.equals(pix)) {
					pix = new Color(image.getRGB(i, findy));
					++findy;
				}
				y[i] = image.getHeight()-findy;
			}
			previousheight = y[599];
			// Finds bird position
			birdx = 0;
			birdy = 0;
			out:
			for (birdx=0; birdx < image.getWidth(); birdx++) {
				for (birdy=0; birdy < image.getHeight(); birdy++) {
					pix = new Color(image.getRGB(birdx, birdy));
					if (birdcolor.equals(pix)) {
						break out;
					}
				}
			}

			birdx += radius;
			birdy = image.getHeight()- birdy;
			if (stamp[0] != previousframe+1) {
				framesmissed.addAndGet(stamp[0]-(previousframe+1));
				missed = stamp[0]-previousframe;
			} else {
				missed = 0;
			}

			previousframe = stamp[0];
			//decision process
			if (((birdy-2*radius)>y[birdx])&&pressed.get()){
				pressed.set(true);
				return true;
			}
			if(birdx>597){
				return true;
			}
			slope = (-((double)y[birdx+2])+8.0*((double)y[birdx+1])-8.0*((double)y[birdx-1])+((double)y[birdx-2]))/12.0;

			if (((birdy-radius)<=y[birdx])&&(slope>0)){
				pressed.set(false);
				distribution = r.nextGaussian();
				return true;
			}

			// Find bird speed
			movedx = 0;
			while (previousheight!=y[599-movedx]) {
				movedx += 1;
			}

			movedx += birdx-previousx;
			speedx = movedx/(missed+1);
			speedy = (previousy-birdy)/(missed+1);
			// Find bird landing position
			landx = birdx;
			landy = birdy;
			time = 0;
			while (landy>y[(int) Math.round(landx)]){
				++ time;
				landx += speedx*time;
				landy = landy + speedy*time - 0.5*g*time*time;
				if (landx>597) {
					return true;
				}
			}

			int landxint = (int) Math.round(landx);
			// Account for AI skill
			landx += skill*distribution;
			// Find bird landing slope
			landslope = (-((double)y[landxint+2])+8.0*((double)y[landxint+1])-8.0*((double)y[landxint-1])+((double)y[landxint-2]))/12.0;
			// Finish deciding
			if (landslope<0.2) {
				pressed.set(true);
			} else {
				pressed.set(false);
			}

			//long endtime = System.nanoTime();
			//System.out.println(endtime-starttime);
		}
		return true;
	}	
}
