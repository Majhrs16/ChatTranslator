package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import majhrs16.cht.ChatTranslator;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.translator.api.Core;

public class util {
	private static ChatTranslator plugin = ChatTranslator.getInstance();
	private static Pattern version       = Pattern.compile("\\d\\.\\d\\.\\d");

	public static double getMinecraftVersion() {
		Matcher matcher = version.matcher(Bukkit.getVersion());

		if (matcher.find()) {
			String version = matcher.group(0);
			version = version.substring(0, version.lastIndexOf("."));

			return Double.parseDouble(version);
		}

		return 0.0;
	}

	public static boolean IF(FileConfiguration cfg, String path) {
			// Comprobador rapido si existe una configuracion.y si es true.

		return cfg.contains(path) && cfg.getBoolean(path);
	}

	public static void assertLang(String lang, String text) {
			// Si no esta soportado lang lanza una excepcion IllegalArgumentException junto con text. 

		if (!Core.GT.isSupport(lang)) {
			throw new IllegalArgumentException(text);
		}
	}

	public static void assertLang(String lang) {
		assertLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
	}

	public static Message getDataConfigDefault() {
		Message from = new Message();
			from.setTo(null); // Necesario para evitar crashes.
			from.setSender(Bukkit.getConsoleSender());
			from.setMessageFormat("%ct_messages%");
			from.setLangSource("es");
			from.setLangTarget(ChatTranslatorAPI.getInstance().getLang(Bukkit.getConsoleSender()));
			from.setColor(true);
			from.setFormatPAPI(false);

		return from;
	}

	public static Message createChat(CommandSender sender, String messages, String langSource, String langTarget, String path) {
		FileConfiguration config = plugin.getConfig();

		if (path == null)
			path = "";

		else
			path = "_" + path;

		String _path = "formats.to" + path;
		Message to = new Message(
			null,
			sender,
			config.contains(_path + ".messages") ? String.join("\n", config.getStringList(_path + ".messages")).replace("\\t", "\t") : null,
			messages,
			config.contains(_path + ".toolTips") ? String.join("\n", config.getStringList(_path + ".toolTips")).replace("\\t", "\t") : null,
			config.contains(_path + ".sounds")   ? String.join("\n", config.getStringList(_path + ".sounds")).replace("\\t", "\t")   : null,
			false,

			langSource,
			langTarget,

			IF(config, "chat-color-personalized"),
			IF(config, "use-PAPI-format")
		);

		_path = "formats.from" + path;
		Message from = new Message(
			to,
			sender,
			config.contains(_path + ".messages") ? String.join("\n", config.getStringList(_path + ".messages")).replace("\\t", "\t") : null,
			messages,
			config.contains(_path + ".toolTips") ? String.join("\n", config.getStringList(_path + ".toolTips")).replace("\\t", "\t") : null,
			config.contains(_path + ".sounds")   ? String.join("\n", config.getStringList(_path + ".sounds")).replace("\\t", "\t")   : null,
			false,

			langSource,
			langTarget,

			IF(config, "chat-color-personalized"),
			IF(config, "use-PAPI-format")
		);

		return from;
	}

	public static String wrapText(String text, int maxLength) {
		if (text.length() <= maxLength)
			return text;

		ArrayList<String> segments = new ArrayList<>();
		int currentIndex = 0;
		while (currentIndex < text.length()) {
			int endIndex = Math.min(currentIndex + maxLength, text.length());
			segments.add(text.substring(currentIndex, endIndex));
			currentIndex = endIndex;
		}

		return String.join("\n", segments);
	}
}