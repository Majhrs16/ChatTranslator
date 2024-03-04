package majhrs16.cht.util.cache.internal;

import majhrs16.cht.translator.ChatTranslatorAPI;
import org.bukkit.configuration.file.FileConfiguration;

import majhrs16.cht.events.custom.Message;
import majhrs16.dst.DiscordTranslator;
import majhrs16.cot.CoreTranslator;
import majhrs16.cht.ChatTranslator;
import majhrs16.lib.Kernel;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Texts {
	private static Map<String, Object> dataMap;
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("%(.+?)%");

	public static void reload() {
		dataMap                  = new HashMap<>();
		FileConfiguration config = ChatTranslator.getInstance().formats.get();

		for (String key : config.getKeys(true))
			dataMap.put(key, config.get(key));

		dataMap.put("versions.plugin", "b" + ChatTranslator.getInstance().getDescription().getVersion());
		dataMap.put("versions.dst", DiscordTranslator.version);
		dataMap.put("versions.cot", CoreTranslator.version);
		dataMap.put("versions.kernel", Kernel.version);

		dataMap.put("plugin.url", "https://www.spigotmc.org/resources/chattranslator.106604/");

		formatKeysWithVariables();
	}

	@Deprecated
	public static String getString(String key) {
		return String.join("\n", get(key));
	}

	public static String[] get(String key) {
		List<String> result = new ArrayList<>();

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
		for (Map.Entry<String, Object> entry : dataMap.entrySet())
			entry.setValue(formatStringWithVariables(get(entry.getKey())));
	}

	private static String[] formatStringWithVariables(String[] inputs) {
		String[] newInputs = inputs.clone();

		for (int i = 0; i < newInputs.length; i++) {
			Matcher matcher = VARIABLE_PATTERN.matcher(newInputs[i]);

			while (matcher.find()) {
				String key = matcher.group(1);

				if (dataMap.containsKey(key)) {
					String[] value = new Message().format(key).getMessages().getFormats();

					if (value.length == 0)
						value = get(key);

					newInputs = ChatTranslatorAPI.getInstance().replaceArray(newInputs, matcher.group(0), value);
					matcher   = VARIABLE_PATTERN.matcher(newInputs[i]);
				}
			}
		}

		return newInputs;
	}
}