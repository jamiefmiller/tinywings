package tinywings;

public class PerformanceTest {

	public static void main(String[] args) {
		for (int fps=160; fps<10000; fps*=2) {
			Performance perf = Main.runConcurrentGame(fps);
			double missed = perf.getLateFrameFraction();
			System.out.println(fps + " " + missed);
		}
	}
}
