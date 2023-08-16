package majhrs16.cht.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.translator.API.API;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

public class Chat implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API = new API();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMessage(AsyncPlayerChatEvent event) {
		if (!plugin.enabled || event.isCancelled())
			return;

		FileConfiguration config = plugin.getConfig();

		if (util.IF(config, "show-native-chat.cancel-event")) {
			event.setCancelled(true);
		}

		String path = "formats.";
		Message console  = util.createMessage(null,     Bukkit.getConsoleSender(), event.getMessage(), false, API.getLang(Bukkit.getConsoleSender()), path + "console");
		Message to_model = util.createMessage(console,  null,                      event.getMessage(), false, null,                                   path + "to");
		Message from     = util.createMessage(to_model, event.getPlayer(),         event.getMessage(), false, API.getLang(event.getPlayer()),         path + "from");

		API.broadcast(from);

		if (util.IF(config, "show-native-chat.clear-recipients")) {
			event.getRecipients().clear();
		}
	}
}