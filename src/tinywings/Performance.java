package tinywings;

public class Performance {
	private double lateFrameFraction;
	private double missedFrameFraction;
	
	Performance(double lateFrameFraction, double missedFrameFraction) {
		this.lateFrameFraction = lateFrameFraction;
		this.missedFrameFraction = missedFrameFraction;
	}
	
	double getLateFrameFraction() {
		return lateFrameFraction;
	}
	
	double getMissedFrameFraction() {
		return missedFrameFraction;
	}
}
