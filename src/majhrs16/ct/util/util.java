package majhrs16.ct.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.GoogleTranslator;

public class util {
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
	
	public static int stringCount(String text, String search) {
		int count = 0;
		int i = text.indexOf(search);
		while (i != -1) {
			i = text.indexOf(search, i + 1);
			count++;
		}

		return count;
	}

	public static Message getDataConfigConsole() {
		Message father = new Message();
			father.setPlayer(Bukkit.getConsoleSender());
			father.setMessageFormat("$ct_messages$");
			father.setShow(false);
			father.setLang("es");
			father.setColorPersonalized(true);
			father.setFormatMessage(false);

			Message msg = new Message();
				msg.setFather(father);
				msg.setMessageFormat("$ct_messages$");
				msg.setShow(true);
				msg.setColorPersonalized(true);
				msg.setFormatMessage(false);
		return msg;
	}
}
