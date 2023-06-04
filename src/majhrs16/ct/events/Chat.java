package majhrs16.ct.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.util.util;

public class Chat implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API = new API();

	public Chat(ChatTranslator plugin) {}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onMessage(AsyncPlayerChatEvent event) {
		if (!plugin.enabled || event.isCancelled())
			return;

		String path;
		FileConfiguration config = plugin.getConfig();

		path = "formats.from";
		Message father = new Message(
			null,
			event.getPlayer(),
			config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
			event.getMessage(),
			config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
			config.contains(path + ".sounds") ? String.join("\n", config.getStringList(path + ".sounds"))     : null,
			true,

			API.getLang(event.getPlayer()),

			util.IF(config, "chat-color-personalized"),
			util.IF(config, "use-PAPI-format")
		);
		
		Message from = father.clone();
			from.setFather(father);
			from.setShow(false);

		path = "formats.to";
		Message to_model = new Message(
			from,
			null,
			config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
			from.getMessages(),
			config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
			config.contains(path + ".sounds") ? String.join("\n", config.getStringList(path + ".sounds"))     : null,
			true,

			null,

			util.IF(config, "chat-color-personalized"),
			util.IF(config, "use-PAPI-format")
		);

		API.broadcast(from, to_model);

		if (!util.IF(plugin.getConfig(), "show-native-chat")) {
			event.getRecipients().clear();
		}
	}
}
