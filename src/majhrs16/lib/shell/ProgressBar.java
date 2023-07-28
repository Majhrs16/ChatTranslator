package majhrs16.lib.shell;

import majhrs16.lib.utils.Str;

public class ProgressBar {
	private int max;
	private int width;
	private double i;

	public ProgressBar(int max, int width) {
		this.max = max;
		this.width = width;
		this.i = 0;
	}

	public ProgressBar(int max) {
		this(max, 50);
	}

	public void step(int i) {
		set(this.i + i);
	}

	public double get(double percentage) {
		return percentage * i / max;
	}

	public String show() {
		double percent = get(100.0);
		int bar = (int) Math.round(percent * width / 100.0);
		return "\r[" + Str.repeat("█", bar) + Str.repeat("-", width - bar) + "] " + String.format("%.2f", percent) + "%";
	}

	public void set(double i) {
		this.i = i;
	}
}