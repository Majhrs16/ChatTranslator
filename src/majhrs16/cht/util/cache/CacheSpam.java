package majhrs16.cht.util.cache;

public class CacheSpam {
	Float count;
	Float max;

	public CacheSpam(Float count, Float max) {
		if (count != null) setCount(count);
		if (max != null)   setMax(max);
	}

	public void setCount(Float count) {
		this.count = count;
	}

	public void setMax(Float max) {
		this.max = max;
	}


	public Float getMax() {
		return max;
	}

	public Float getMaxInt() {
		return (float) max.intValue();
	}
	
	public Float getMaxFloat() {
		return max - getMaxInt();
	}

	
	public Float getCount() {
		return count;
	}
	
	public Float getCountInt() {
		return (float) count.intValue();
	}
	
	public Float getCountFloat() {
		return count - getCountInt();
	}
}