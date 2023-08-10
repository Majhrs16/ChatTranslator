package majhrs16.cht.events;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

public class EntryPlayer implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API = new API();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onExit(PlayerJoinEvent event) {
		if (!plugin.enabled)
			return;

		String path = "formats.";
		Message from     = util.createMessage(null, Bukkit.getConsoleSender(), event.getJoinMessage(), true,  "en", path + "from_entry");
		Message console  = util.createMessage(from, Bukkit.getConsoleSender(), event.getJoinMessage(), false, API.getLang(Bukkit.getConsoleSender()), path + "to_entry");
		Message to_model = util.createMessage(from, null,                      event.getJoinMessage(), false, null, path + "to_entry");

		event.setJoinMessage("");

		API.broadcast(to_model, console);
	}
}