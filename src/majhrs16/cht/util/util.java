package majhrs16.cht.util;

import org.bukkit.configuration.file.FileConfiguration;
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
		Message father = new Message();
			father.setPlayer(Bukkit.getConsoleSender());
			father.setMessageFormat("$ct_messages$");
			father.setCancelledThis(true);
			father.setLang("es");
			father.setColorPersonalized(true);
			father.setFormatMessage(false);

			Message msg = new Message();
				msg.setFather(father);
				msg.setMessageFormat("$ct_messages$");
				msg.setCancelledThis(false);
				msg.setColorPersonalized(true);
				msg.setFormatMessage(false);
		return msg;
	}

	public static Message createMessage(Message from, CommandSender to_player, String messages, Boolean isCancelled, String lang, String path) {
		FileConfiguration config = plugin.getConfig();

		return new Message(
			from,
			to_player,
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
}
