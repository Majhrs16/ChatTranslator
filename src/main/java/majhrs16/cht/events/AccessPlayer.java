package majhrs16.cht.events;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.util.updater.UpdateChecker;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.dst.utils.DiscordChat;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public class AccessPlayer implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();

	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntry(PlayerJoinEvent event) {
		onAccess(
			event.getPlayer(),
			"entry",
			event::setJoinMessage,
			event.getJoinMessage()
		);

		if (Config.CHECK_UPDATES.IF() && Permissions.ChatTranslator.ADMIN.IF(event.getPlayer()))
			new UpdateChecker(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onExit(PlayerQuitEvent event) {
		onAccess(
			event.getPlayer(),
			"exit",
			event::setQuitMessage,
			event.getQuitMessage()
		);
	}

	private void onAccess(Player player, String MF, Consumer<String> messageSetter, String originalMessage) {
		if (plugin.isDisabled() || !Config.TranslateOthers.ACCESS.IF())
			return;

		messageSetter.accept("");

		Message model = util.createChat(
			player,
			new String[] { originalMessage },
			util.getNativeLang(),
			API.getLang(player),
			MF
		);

		Message console = util.createChat(
				Bukkit.getConsoleSender(),
				new String[] { originalMessage },
				util.getNativeLang(),
				API.getLang(Bukkit.getConsoleSender()),
				MF + "_console")
			.setSender(player)
			.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(model, util.getOnlinePlayers(), froms -> {
			froms.add(console);
			API.broadcast(froms, API::sendMessage); // Evitar el ChatLimiter y por ende notificar a todos.
		});

		notifyToDiscord(player, MF + "_discord", originalMessage);
	}

	public void notifyToDiscord(Player player, String MF, String originalMessage) {
		if (!Config.TranslateOthers.DISCORD.IF())
			return;

		Message model_discord = util.createChat(
			player,
			new String[] { originalMessage },
			util.getNativeLang(),
			plugin.storage.getDefaultLang(),
			MF
		);

		Message to = API.formatMessage(model_discord).getTo();

		if (to.getMessages().getFormats().length == 0)
			return;

		DiscordChat.broadcastEmbed(
			DiscordChat.getChannels("discord.channels.player-access"),
			to.getMessages().getFormats(),
			to.getToolTips().getFormats(),
			Integer.parseInt(Texts.get("to_" + MF + ".color")[0].substring(1), 16)
		);
	}
}