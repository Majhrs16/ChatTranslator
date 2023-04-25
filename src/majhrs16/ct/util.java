package majhrs16.ct;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;

@SuppressWarnings("unused")
public class util {
	private main plugin;
	public static ArrayList<AsyncPlayerChatEvent> chat = new ArrayList<AsyncPlayerChatEvent>();

	public util(main plugin) {
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
	
	public String getUUID(CommandSender sender) {
		String UUID = "";
		
		try {
			UUID += ((Player) sender).getUniqueId();

		} catch (ClassCastException e) {
			UUID += "0";
		}
		
		return UUID;
	}
	
	public boolean IF(FileConfiguration cfg, String path) {
		return cfg.contains(path) && cfg.getString(path).equals("true");
	}

	public boolean IF(String path) {
		return IF(plugin.getConfig(), path);
	}

	public AsyncPlayerChatEvent popChat(int i) {
		AsyncPlayerChatEvent event;
		try {
			event = chat.get(i);
			chat.remove(i);

		} catch (IndexOutOfBoundsException e) {
			event = null;
		}

		return event;
	}
}
