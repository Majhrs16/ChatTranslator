package majhrs16.lib.utils;

public class Str {
    public static String repeat(String text, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(text);
        }
        return sb.toString();
    }

    public static String center(String s, int length) {
        int leftPadding = (length - s.length()) / 2;
        return String.format("%" + leftPadding + "s%s%" + leftPadding + "s", "", s, "");
    }

    public static String rjust(String text, int width, String fill) {
        if (text.length() >= width) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int numSpaces = width - text.length();

        for (int i = 0; i < numSpaces; i++) {
            result.append(fill);
        }

        result.append(text);
        return result.toString();
    }

    public static String ljust(String text, int width, String fill) {
        if (text.length() >= width) {
            return text;
        }

        StringBuilder result = new StringBuilder(text);

        while (result.length() < width) {
            result.append(fill);
        }

        return result.toString();
    }
    
	public static int count(String text, String search) {
		int count = 0;
		int i = text.indexOf(search);
		while (i != -1) {
			i = text.indexOf(search, i + 1);
			count++;
		}

		return count;
	}
}
