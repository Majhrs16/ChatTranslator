package majhrs16.ct;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;

@SuppressWarnings("unused")
public class Util  {
	private Main plugin;
	public static ArrayList<AsyncPlayerChatEvent> chat = new ArrayList<AsyncPlayerChatEvent>();

	public Util(Main plugin) {
		this.plugin = plugin;
	}

	public Boolean checkPAPI() {
		Boolean havePAPI = null;

		try {
		    Class.forName("me.clip.placeholderapi.PlaceholderAPI");
		    havePAPI = true;
		} catch (ClassNotFoundException e) {
		    havePAPI = false;
		}

		return havePAPI;
	}
	
	public boolean IF(FileConfiguration cfg, String path) {
		// Comprobador rapido si existe y si es true una configuracion.
		return cfg.contains(path) && cfg.getString(path).equals("true");
	}

	public boolean IF(String path) {
		return IF(plugin.getConfig(), path);
	}

	public AsyncPlayerChatEvent popChat(int i) {
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
	
	public void checkSupportLang(String lang, String text) {
		if (new GoogleTranslator().getCode(lang) == null) {
			throw new IllegalArgumentException(text);
		}
	}
	
	public void checkSupportLang(String lang) {
		checkSupportLang(lang, "El lenguaje '" + lang + "' no esta soportado.");
	}
}
