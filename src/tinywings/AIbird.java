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
		y = new int[screen.getReference().getWidth()];
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
		int previousx;
		double time;
		int previousheight = 0;
		double slope;
		outer:
		while(true) {
			int[] stamp = new int[1];
			stamp[0] = 0;
			BufferedImage image = screen.get(stamp);
			if (stamp[0] < 0){
				break;
			}
			if (stamp[0] > previousframe) {
				System.out.println(pressed.get());
				previousy = birdy;
				previousx = birdx;
				//System.out.println("prepoopy");
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
				//System.out.println("poopy");
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
				//System.out.println("poopy2");
				birdx += radius;
				birdy = image.getHeight()- birdy;
				if (stamp[0] != previousframe+1) {
					framesmissed.addAndGet(stamp[0]-(previousframe+1));
					missed = stamp[0]-previousframe;
				} else {
					missed = 0;
				}
				//System.out.println("poopy3");
				previousframe = stamp[0];
				//decision process
				if (((birdy-2*radius)>y[birdx])&&pressed.get()){
					pressed.set(true);
					continue;
				}
				if(birdx>597){
					break;
				}
				slope = (-((double)y[birdx+2])+8.0*((double)y[birdx+1])-8.0*((double)y[birdx-1])+((double)y[birdx-2]))/12.0;
				//System.out.println(y[birdx]);
				//System.out.println("s1 "+slope);
				if (((birdy-radius)<=y[birdx])&&(slope>0)){
					pressed.set(false);
					distribution = r.nextGaussian();
					continue;
				}
				//System.out.println("poopy4");
				// Find bird speed
				movedx = 0;
				while (previousheight!=y[599-movedx]) {
					movedx += 1;
				}
				//System.out.println("poopy5");
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
						continue outer;
					}
				}
				//System.out.println("poopy6");
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
				//System.out.println("s2 "+landslope);
			}
		}
	}	
}
