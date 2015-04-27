package tinywings;

public class PerformanceTest {

	public static void main(String[] args) {
		for (int fps=450; fps<500; fps+=50) {
			for (int i=0; i<3; i++) {
			Performance perf = Main.runConcurrentGame(fps);
			double late = perf.getLateFrameFraction();
			double missed = perf.getMissedFrameFraction();
			
			System.out.println(fps + "\t" + late + "\t" + missed);
			}
		}
	}
}
