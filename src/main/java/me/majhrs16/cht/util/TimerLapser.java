package me.majhrs16.cht.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerLapser {
	private long endTime;
	private long startTime;
	private int local_iterations;

	private static final AtomicInteger global_iterations = new AtomicInteger(0);

	private String format = "Time Elapsed: {seconds}s, {milliseconds}ms, {nanoseconds}ns\n\tIterations: {global}.{local}\n\n";

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	public TimerLapser() {
		global_iterations.getAndIncrement();
		start();

	}

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	public TimerLapser(Runnable code) {
		global_iterations.getAndIncrement();
		analyzeCode(code);
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void analyzeCode(Runnable code) {
		start();
		try { code.run(); }
		finally { stop(); }
	}

	public long getActualElapsed(TimeUnit unit) {
		long nanoseconds = endTime - startTime;
		return unit.convert(nanoseconds, TimeUnit.NANOSECONDS);
	}

	public void start() {
		endTime   = 0; // Reset endTime
		startTime = System.nanoTime();
	}

	public void stop() {
		endTime = System.nanoTime();
		local_iterations ++;
	}

	public String getResults() {
		if (endTime == 0)
			throw new IllegalStateException("Timer not stopped");

		long milliseconds = getActualElapsed(TimeUnit.MILLISECONDS);
		long nanoseconds = getActualElapsed(TimeUnit.NANOSECONDS);
		long seconds = getActualElapsed(TimeUnit.SECONDS);

		return format
			.replace("{seconds}", String.valueOf(seconds))
			.replace("{nanoseconds}", String.valueOf(nanoseconds))
			.replace("{milliseconds}", String.valueOf(milliseconds))
			.replace("{global}", String.valueOf(getGlobalIterations()))
			.replace("{local}", String.valueOf(getLocalIterations()));
	}

	public void showResults() {
		System.out.println(getResults());
	}

	public long getGlobalIterations() {
		return global_iterations.get();
	}

	public long getLocalIterations() {
		return local_iterations;
	}
}