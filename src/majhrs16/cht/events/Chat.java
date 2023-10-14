package majhrs16.cht.events;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

public class Chat implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMessage(AsyncPlayerChatEvent event) {
		if (plugin.isDisabled() || event.isCancelled())
			return;

		if (Config.NativeChat.CANCEL.IF()) {
			event.setCancelled(true);
		}

		String from_lang = API.getLang(event.getPlayer());

		Message to_model = util.createChat(event.getPlayer(), event.getMessage(), from_lang, from_lang, null);

		Message from_console = to_model.clone();
			Message console  = util.createChat(
				Bukkit.getConsoleSender(), event.getMessage(), from_lang, API.getLang(Bukkit.getConsoleSender()), "console");

			from_console.setTo(console.getTo()); // Une el from del to_model con el to del console.
			from_console.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(to_model, froms -> froms.add(from_console));

		if (Config.NativeChat.CLEAR.IF()) {
			event.getRecipients().clear();
		}
	}
}