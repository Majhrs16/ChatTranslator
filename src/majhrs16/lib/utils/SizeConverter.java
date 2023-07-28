package majhrs16.lib.utils;

import java.util.HashMap;
import java.util.Map;

public class SizeConverter {
	private static Map<Boolean, String[]> suffixesMap = new HashMap<>();

	static {
		suffixesMap.put(false, new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"});
		suffixesMap.put(true, new String[]{"b", "Kb", "Mb", "Gb", "Tb", "Pb", "Eb", "Zb", "Yb"});
	}

	private static Object[] _getSize(double num, Integer max, boolean bits) {
		if (num < 0) {
			throw new IllegalArgumentException("Value must be greater than or equal to 0");
		}

		int multiple = bits ? 1000 : 1024;
		String[] suffixes = suffixesMap.get(bits);

		for (int i = 0; i < suffixes.length; i++) {
			if ((max != null && i == max) || num <= multiple) {
				return new Object[]{num, suffixes[i]};
			}
			num /= multiple;
		}

		throw new IllegalArgumentException("Value is too large to convert");
	}

	public static String getSize(double num, Integer max, boolean bits) {
		Object[] sizeTuple = _getSize(num, max, bits);
		return String.format("%.2f %s", sizeTuple[0], sizeTuple[1]);
	}
}