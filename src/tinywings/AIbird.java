package tinywings;

import java.awt.Color;
import java.awt.image.BufferedImage;
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
	public AIbird(AtomicStampedReference<BufferedImage> screen, Color birdcolor, int radius, int skill, AtomicInteger framesmissed) {
		super(screen);
		this.birdcolor = birdcolor;
		r = new Random();
		this.radius = radius;
		this.skill = skill;
		this.framesmissed = framesmissed;
		// Finds the line
		for(int i = 0; i<screen.getReference().getWidth(); i++){
			int findy = 0;
			while(!(pix == Color.green)) {
				pix = new Color(screen.getReference().getRGB(i, findy));
				++findy;
			}
			y[i] = screen.getReference().getHeight()-findy;
		}
		// Finds bird position
		birdx = 200;
		birdy = 0;
		while(!(birdcolor == pix)) {
			while(birdy<screen.getReference().getHeight()) {
				pix = new Color(screen.getReference().getRGB(birdx, birdy));
				++birdy;
			}
			++birdx;
		}
		birdx += radius;
		birdy = screen.getReference().getHeight()- birdy;
	}

	@Override
	public void run() {
		double distribution = 0;
		double landslope;
		double landx;
		double landy;
		int missed;
		int movedx;
		int previousy;
		int slope;
		while(true) {
			int[] stamp = new int[1];
			stamp[0] = 0;
			BufferedImage image = screen.get(stamp);
			previousy = birdy;
			// Finds the line
			for(int i = 0; i<image.getWidth(); i++){
				int findy = 0;
				while(!(pix == Color.green)) {
					pix = new Color(image.getRGB(i, findy));
					++findy;
				}
				y[i] = image.getHeight()-findy;
			}
			// Finds bird position
			birdx = 200;
			birdy = 0;
			while(!(birdcolor == pix)) {
				while(birdy<image.getHeight()) {
					pix = new Color(image.getRGB(birdx, birdy));
					++birdy;
				}
				++birdx;
			}
			birdx += radius;
			birdy = image.getHeight()- birdy;
			if (stamp[0] < 0){
				break;
			}
			if (stamp[0] != previousframe+1) {
				framesmissed.addAndGet(stamp[0]-(previousframe+1));
				missed = stamp[0]-previousframe;
			} else {
				missed = 0;
			}
			previousframe = stamp[0];
			//decision process
			if (((birdy+radius)>y[birdx])&&pressed.get()){
				pressed.set(true);
				continue;
			}
			slope = (-y[birdx+2]+8*y[birdx+1]-8*y[birdx-1]+y[birdx-2])/12;
			if (((birdy+radius)==y[birdx])&&(slope>0)){
				pressed.set(false);
				distribution = r.nextGaussian();
				continue;
			}
			// Find bird speed
			movedx = 0;
			while (y[birdx]!=y[200-movedx]) {
				movedx -= 1;
			}
			speedx = movedx/(missed+1);
			speedy = (previousy-birdy)/(missed+1);
			// Find bird landing position
			landx = birdx;
			landy = birdy;
			while (landy>y[(int) Math.round(landx)]){
				landx += speedx;
				landy = landy + speedy - 0.5*g;
				if (landx>600) {
					continue;
				}
			}
			int landxint = (int) Math.round(landx);
			// Account for AI skill
			landx += skill*distribution;
			// Find bird landing slope
			landslope = (-y[landxint+2]+8*y[landxint+1]-8*y[landxint-1]+y[landxint-2])/12;
			// Finish deciding
			if (landslope<0.2) {
				pressed.set(true);
			}
		}
	}	
}
