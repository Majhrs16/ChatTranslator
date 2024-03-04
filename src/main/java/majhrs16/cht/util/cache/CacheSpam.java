package majhrs16.cht.util.cache;

public class CacheSpam {
	Double count;
	Double max;

	public CacheSpam(Double count, Double max) {
		if (count != null) setCount(count);
		if (max != null)   setMax(max);
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public void setMax(Double max) {
		this.max = max;
	}


	public Double getMax() {
		return max;
	}

	public int getMaxInt() {
		return max.intValue();
	}
	
	public Double getMaxDouble() {
		return max - getMaxInt();
	}

	
	public Double getCount() {
		return count;
	}
	
	public int getCountInt() {
		return count.intValue();
	}
	
	public Double getCountDouble() {
		return count - getCountInt();
	}
}