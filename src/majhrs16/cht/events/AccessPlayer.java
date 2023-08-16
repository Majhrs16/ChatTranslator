package majhrs16.cht.events;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

public class AccessPlayer implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API = new API();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onExit(PlayerJoinEvent event) {
		if (!plugin.enabled)
			return;

		String path = "formats.";
		Message console  = util.createMessage(null,     Bukkit.getConsoleSender(), event.getJoinMessage(), false, API.getLang(Bukkit.getConsoleSender()), path + "to_entry");
		Message to_model = util.createMessage(console,  null,                      event.getJoinMessage(), false, null, path + "to_entry");
		Message from     = util.createMessage(to_model, Bukkit.getConsoleSender(), event.getJoinMessage(), true,  "en", path + "from_entry");

		event.setJoinMessage("");

		API.broadcast(from);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntry(PlayerQuitEvent event) {
		if (!plugin.enabled)
			return;

		String path = "formats.";
		Message console  = util.createMessage(null,     Bukkit.getConsoleSender(), event.getQuitMessage(), false, API.getLang(Bukkit.getConsoleSender()), path + "to_exit");
		Message to_model = util.createMessage(console,  null,                      event.getQuitMessage(), false, null, path + "to_exit");
		Message from     = util.createMessage(to_model, Bukkit.getConsoleSender(), event.getQuitMessage(), true,  "en", path + "from_exit");

		event.setQuitMessage("");

		API.broadcast(from);
	}
}