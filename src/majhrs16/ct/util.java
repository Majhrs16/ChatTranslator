package majhrs16.ct;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import majhrs16.ct.translator.GoogleTranslator;

public class util  {
	public static ArrayList<AsyncPlayerChatEvent> chat = new ArrayList<AsyncPlayerChatEvent>();

	public static Boolean checkPAPI() {
		// Comprueba si esta disponible PAPI.

		Boolean havePAPI = null;

		try {
		    Class.forName("me.clip.placeholderapi.PlaceholderAPI");
		    havePAPI = true;
		} catch (ClassNotFoundException e) {
		    havePAPI = false;
		}

		return havePAPI;
	}
	
	public static boolean IF(FileConfiguration cfg, String path) {
		// Comprobador rapido si existe y si es true una configuracion.
		return cfg.contains(path) && cfg.getString(path).equals("true");
	}

	public static AsyncPlayerChatEvent popChat(int i) {
		// Elimina el primer elemento de la cola del chat.
		AsyncPlayerChatEvent event;
		try {
			event = chat.get(i);
			chat.remove(i);

		} catch (IndexOutOfBoundsException e) {
			event = null;
		}

		return event;
	}

	public static String assertLang(String lang, String text) {
		if (!new GoogleTranslator().isSupport(lang)) {
			throw new IllegalArgumentException(text);
		}

		return lang;
	}

	public static String assertLang(String lang) {
		return assertLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
	}
}
