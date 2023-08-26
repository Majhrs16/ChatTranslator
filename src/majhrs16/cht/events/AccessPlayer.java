package majhrs16.cht.events;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import majhrs16.cht.events.custom.Message;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.bool.Config;
import majhrs16.cht.bool.Permissions;
import majhrs16.cht.translator.API;
import majhrs16.cht.util.Updater;
import majhrs16.cht.util.util;

public class AccessPlayer implements Listener {
	private ChatTranslator plugin = ChatTranslator.plugin;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntry(PlayerJoinEvent event) {
		if (!plugin.enabled || !Config.TranslateOthers.ACCESS.IF())
			return;

//		Message console  = util.createChat(Bukkit.getConsoleSender(), event.getJoinMessage(), "en", API.getLang(Bukkit.getConsoleSender()), "entry");
//			console.setCancelledThis(true); // Evitar duplicacion del mensaje.
		Message to_model = util.createChat(Bukkit.getConsoleSender(), event.getJoinMessage(), "en", API.getLang(Bukkit.getConsoleSender()), "entry");

		event.setJoinMessage("");

		API.broadcast(to_model);

		if (Permissions.chattranslator.ADMIN.IF(event.getPlayer()))
			new Updater().updateChecker(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onExit(PlayerQuitEvent event) {
		if (!plugin.enabled || !Config.TranslateOthers.ACCESS.IF())
			return;

//		Message console  = util.createChat(Bukkit.getConsoleSender(), event.getQuitMessage(), "en", API.getLang(Bukkit.getConsoleSender()), "exit");
//			console.setCancelledThis(true); // Evitar duplicacion del mensaje.
		Message to_model = util.createChat(Bukkit.getConsoleSender(), event.getQuitMessage(), "en", API.getLang(Bukkit.getConsoleSender()), "exit");

		event.setQuitMessage("");

		API.broadcast(to_model); // tos -> tos.add(console));
	}
}