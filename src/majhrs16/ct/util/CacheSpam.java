package majhrs16.ct.util;


public class CacheSpam {
	Float max;
	Float count; 

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
		return Float.valueOf(max.intValue());
	}
	
	public Float getMaxFloat() {
		return max - getMaxInt();
	}

	
	public Float getCount() {
		return count;
	}
	
	public Float getCountInt() {
		return Float.valueOf(count.intValue());
	}
	
	public Float getCountFloat() {
		return count - getCountInt();
	}
}
