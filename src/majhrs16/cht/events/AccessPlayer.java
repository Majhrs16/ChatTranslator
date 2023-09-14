package majhrs16.cht.events;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.Updater;
import majhrs16.cht.util.util;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.util.cache.internal.Texts;

public class AccessPlayer implements Listener {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntry(PlayerJoinEvent event) {
		if (plugin.isDisabled() || !Config.TranslateOthers.ACCESS.IF())
			return;

		event.setJoinMessage("");

		Message DC = util.getDataConfigDefault();

		Message to_model = util.createChat(event.getPlayer(), Texts.EVENTS.ACCESS.ENTRY, DC.getLangSource(), API.getLang(event.getPlayer()), "entry");

		Message from_console = to_model.clone();
			Message console  = util.createChat(Bukkit.getConsoleSender(), Texts.EVENTS.ACCESS.ENTRY, DC.getLangSource(), API.getLang(Bukkit.getConsoleSender()), "entry");

			from_console.setTo(console.getTo()); // Une el from del to_model con el to del console.
			from_console.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(to_model, froms -> froms.add(from_console));

		if (Config.CHECK_UPDATES.IF() && Permissions.chattranslator.ADMIN.IF(event.getPlayer()))
			new Updater().checkUpdate(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onExit(PlayerQuitEvent event) {
		if (plugin.isDisabled() || !Config.TranslateOthers.ACCESS.IF())
			return;

		event.setQuitMessage("");

		Message DC = util.getDataConfigDefault();

		Message to_model = util.createChat(event.getPlayer(), Texts.EVENTS.ACCESS.EXIT, DC.getLangSource(), API.getLang(event.getPlayer()), "exit");

		Message from_console = to_model.clone();
			Message console  = util.createChat(Bukkit.getConsoleSender(), Texts.EVENTS.ACCESS.EXIT, DC.getLangSource(), API.getLang(Bukkit.getConsoleSender()), "exit");

			from_console.setTo(console.getTo()); // Une el from del to_model con el to del console.
			from_console.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(to_model, froms -> froms.add(from_console));
	}
}