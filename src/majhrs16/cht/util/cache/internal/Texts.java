package majhrs16.cht.util.cache.internal;

import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.ChatTranslator;
import majhrs16.lib.BaseLibrary;

import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Texts {
	private static Map<String, Object> dataMap;
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("%(.*?)%");

	public static void reload() {
		dataMap                 = new HashMap<>();
		FileConfiguration config = ChatTranslator.getInstance().messages.get();

		for (String key : config.getKeys(true)) {
			Object value = config.get(key);
			dataMap.put(key, value);
		}

		dataMap.put("versions.plugin", "b" + ChatTranslator.getInstance().getDescription().getVersion());
		dataMap.put("versions.kernel", "" + BaseLibrary.version);
		dataMap.put("versions.dst", "" + DiscordTranslator.version);

		dataMap.put("plugin.url", "https://www.spigotmc.org/resources/chattranslator.106604/");

		formatKeysWithVariables();
	}

	public static String getString(String key) {
		return String.join("\n", get(key));
	}

	public static String[] get(String key) {
		List<String> result = new ArrayList<String>();

		Object value = dataMap.get(key);

		if (value instanceof String) {
			result.add(((String) value).replace("\\t", "\t"));

		} else if (value instanceof List) {
			for (Object sub_value : (List<?>) value) {
				result.add(((String) sub_value).replace("\\t", "\t"));
			}

		} else if (value instanceof String[]) {
			return (String[]) value;
		}

		return result.toArray(new String[0]);
	}

	private static void formatKeysWithVariables() {
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			entry.setValue(formatStringWithVariables(getString(entry.getKey())));
		}
	}

	private static String formatStringWithVariables(String input) {
		Matcher matcher;
		while ((matcher = VARIABLE_PATTERN.matcher(input)).find()) {
			Object value = dataMap.get(matcher.group(1));

			if (value == null)
				break;

			input = input.replace(matcher.group(0), value.toString());
		}

		return input;
	}
}