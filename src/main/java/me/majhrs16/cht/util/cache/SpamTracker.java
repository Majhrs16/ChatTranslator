package me.majhrs16.cht.util.cache;

public class SpamTracker {
	private final int[] counts = new int[2];
	private final int[] max = new int[2];

	public SpamTracker(int actualCount, int totalCount, int actualMax, int totalMax) {
		setActualCount(actualCount);
		setTotalCount(totalCount);
		setActualMax(actualMax);
		setTotalMax(totalMax);
	}

	// Setters
	public void setActualCount(int actualCount) {
		this.counts[0] = actualCount;
	}

	public void setTotalCount(int totalCount) {
		this.counts[1] = totalCount;
	}

	public void setActualMax(int actualMax) {
		this.max[0] = actualMax;
	}

	public void setTotalMax(int totalMax) {
		this.max[1] = totalMax;
	}

	// Getters
	public int getActualCount() {
		return counts[0];
	}

	public int getTotalCount() {
		return counts[1];
	}

	public int getActualMax() {
		return max[0];
	}

	public int getTotalMax() {
		return max[1];
	}
}
