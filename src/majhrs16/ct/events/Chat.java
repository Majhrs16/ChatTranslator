package majhrs16.ct.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.util;

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
		Message from     = createMessage(null, event.getPlayer(), event.getMessage(), false,  API.getLang(event.getPlayer()), path + "from");
		Message to_model = createMessage(from, null,              event.getMessage(), false, null,                           path + "to");

		API.broadcast(to_model); // AGREGAR MESSAGE PARA CONSOLA!!!!!! ////////////////////////////////////////////////////////////////////////////////

		if (util.IF(config, "show-native-chat.clear-recipients")) {
			event.getRecipients().clear();
		}
	}

	public Message createMessage(Message from, CommandSender to_player, String messages, Boolean isCancelled, String lang, String path) {
		FileConfiguration config = plugin.getConfig();

		return new Message(
			from,
			to_player,
			config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
			messages,
			config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
			config.contains(path + ".sounds")   ? String.join("\n", config.getStringList(path + ".sounds"))   : null,
			isCancelled,

			lang,

			util.IF(config, "chat-color-personalized"),
			util.IF(config, "use-PAPI-format")
		);
	}
}