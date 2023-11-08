package majhrs16.cht.events;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.updater.UpdateChecker;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.dst.utils.Utils;
import majhrs16.cht.util.util;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

public class AccessPlayer implements Listener {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntry(PlayerJoinEvent event) {
		if (plugin.isDisabled() || !Config.TranslateOthers.ACCESS.IF())
			return;

		event.setJoinMessage("");

		Message DC = util.getDataConfigDefault();

		Message to_model = util.createChat(event.getPlayer(), Texts.getString("events.access.entry"), DC.getLangSource(), API.getLang(event.getPlayer()), "entry");

		Message from_console = util.createChat(Bukkit.getConsoleSender(), Texts.getString("events.access.entry"), DC.getLangSource(), API.getLang(Bukkit.getConsoleSender()), "entry");
			from_console.setSender(event.getPlayer());
			from_console.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(to_model, util.getOnlinePlayers(), froms -> {
			froms.add(from_console);
			API.broadcast(froms, API::sendMessage); // Evitar el ChatLimiter y por ende notificar a todos.
		});
		notifyToDiscord(to_model, 0x00FF00);

		if (Config.CHECK_UPDATES.IF() && Permissions.ChatTranslator.ADMIN.IF(event.getPlayer()))
			new UpdateChecker(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onExit(PlayerQuitEvent event) {
		if (plugin.isDisabled() || !Config.TranslateOthers.ACCESS.IF())
			return;

		event.setQuitMessage("");

		Message DC = util.getDataConfigDefault();

		Message to_model = util.createChat(event.getPlayer(), Texts.getString("events.access.exit"), DC.getLangSource(), API.getLang(event.getPlayer()), "exit");

		Message from_console = util.createChat(Bukkit.getConsoleSender(), Texts.getString("events.access.exit"), DC.getLangSource(), API.getLang(Bukkit.getConsoleSender()), "exit");
			from_console.setSender(event.getPlayer());
			from_console.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(to_model, util.getOnlinePlayers(), froms -> {
			froms.add(from_console);
			API.broadcast(froms, API::sendMessage); // Evitar el ChatLimiter y por ende notificar a todos.
		});
		notifyToDiscord(to_model, 0xFF0000);
	}

	public void notifyToDiscord(Message DC, int color) {
		if (Config.TranslateOthers.DISCORD.IF()) {
			Message from = new Message();
				from.setSender(DC.getSender());
				from.setMessagesFormats(DC.getMessagesFormats());
				from.setMessages(DC.getMessages());
			from = API.formatMessage(from);

			Message finalFrom = from;
			Utils.broadcast(
				"discord.channels.player-access",
				channel -> Utils.sendMessageEmbed(
					channel,
					ChatColor.stripColor(finalFrom.getMessagesFormats()),
					finalFrom.getToolTips() == null ? null : ChatColor.stripColor(finalFrom.getToolTips()),
					color
				)
			);
		}
	}
}