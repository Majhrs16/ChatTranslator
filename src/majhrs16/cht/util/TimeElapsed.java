package majhrs16.cht.util;

public class TimeElapsed {
	private long startTime;;
	private long endTime;

	private static int counter_global;
	private int counter_local;

	public TimeElapsed() {
		counter_global = counter_global + 1;

		start();
	}

	public TimeElapsed(Runnable codeBlock) {
		counter_global = counter_global + 1;

		start();
		codeBlock.run();
		show();
	}

	public void start() {
		startTime = System.nanoTime();
	}

	public void stop() {
		endTime = System.nanoTime();
	}

	public void show() {
		long nanoseconds = endTime - startTime;
		double milliseconds = (double) nanoseconds / 1_000_000.0;

		System.out.println();
		System.out.println("Tiempo          transcurrido:");
		System.out.println("  nanosegundos: " + nanoseconds);
		System.out.println("  milisegundos: " + milliseconds);
		System.out.println(String.format("Iteracion: %s.%s", counter_global, counter_local));
		System.out.println();

		counter_local  = counter_local + 1;

		start();
	}
}