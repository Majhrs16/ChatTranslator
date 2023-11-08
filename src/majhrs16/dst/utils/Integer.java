package majhrs16.dst.utils;

public class Integer {
	private int integer;

	public Integer(int start) {
		integer = start;
	}

	public int get() {
		return integer;
	}

	public int getAndIncrement() {
		integer++;
		return integer;
	}
}