package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import majhrs16.lib.network.translator.GoogleTranslator;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.events.custom.Message;
// import majhrs16.ct.translator.API.API;

public class util {
	private static ChatTranslator plugin = ChatTranslator.plugin;
//	private API API = new API();

	public static Boolean checKDependency(String dependency) {
		Boolean haveDependency = null;

		try {
			Class.forName(dependency);
			haveDependency = true;

		} catch (ClassNotFoundException e) {
			haveDependency = false;
		}

		return haveDependency;
	}

	public static Boolean checkPAPI() {
			// Comprueba si esta disponible PAPI.

		return checKDependency("me.clip.placeholderapi.PlaceholderAPI");
	}

	public static Boolean checkPL() {
			// Comprueba si esta disponible ProtocolLib.

		return checKDependency("com.comphenix.protocol.ProtocolLib");
	}

	public static boolean IF(FileConfiguration cfg, String path) {
			// Comprobador rapido si existe y si es true una configuracion.

		return cfg.contains(path) && cfg.getString(path).equals("true");
	}

	public static String assertLang(String lang, String text) {
			// Si no esta soportado lang lanza una excepcion IllegalArgumentException junto con text. 

		if (!new GoogleTranslator().isSupport(lang)) {
			throw new IllegalArgumentException(text);
		}

		return lang;
	}

	public static String assertLang(String lang) {
		return assertLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
	}

	public static Message getDataConfigDefault() {
		Message to = new Message();
			to.setMessageFormat("$ct_messages$");
			to.setCancelledThis(false);
			to.setColor(true);
			to.setFormatPAPI(false);

		Message from = new Message();
			from.setTo(to);
			from.setSender(Bukkit.getConsoleSender());
			from.setMessageFormat("$ct_messages$");
			from.setCancelledThis(true);
			from.setLang("es");
			from.setColor(true);
			from.setFormatPAPI(false);

		return from;
	}

	public static Message createMessage(Message to, CommandSender sender, String messages, Boolean isCancelled, String lang, String path) {
		FileConfiguration config = plugin.getConfig();

		return new Message(
			to,
			sender,
			config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")).replace("\\t", "\t") : null,
			messages,
			config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")).replace("\\t", "\t") : null,
			config.contains(path + ".sounds")   ? String.join("\n", config.getStringList(path + ".sounds")).replace("\\t", "\t")   : null,
			isCancelled,

			lang,

			IF(config, "chat-color-personalized"),
			IF(config, "use-PAPI-format")
		);
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