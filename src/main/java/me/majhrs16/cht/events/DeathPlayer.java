package me.majhrs16.cht.events;

import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.dst.utils.DiscordChat;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class DeathPlayer implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		TranslatorBase.LanguagesBase from_lang = util.convertStringToLang("en");
		Player player                          = event.getEntity().getPlayer();
		String message                         = event.getDeathMessage();

		if (message == null) return;

		for (Player to_player : BukkitUtils.getOnlinePlayers())
			message = message.replace(to_player.getName(), "`" + to_player.getName() + "`");

		Message model = util.createChat(
			player,
			new String[] { message },
			from_lang,
			API.getLang(player),
			"death"
		);

		Message console = util.createChat(
				Bukkit.getConsoleSender(),
				new String[] { message },
				from_lang,
				API.getLang(Bukkit.getConsoleSender()),
				"death_console")
			.setSender(player)
			.setCancelledThis(true); // Evitar duplicacion para el remitente.

		API.broadcast(model, BukkitUtils.getOnlinePlayers(), (froms) -> {
			froms.add(console);
			API.broadcast(froms, API::sendMessage);
		});

		if (Config.TranslateOthers.DISCORD.IF()) {
			Message model_discord = util.createChat(
				player,
				new String[] { message },
				util.convertStringToLang("en"),
				plugin.storage.getDefaultLang(),
				"death_discord"
			);

			Message to = API.formatMessage(model_discord).getTo();

			if (to.getMessages().getFormats().length == 0)
				return;

			DiscordChat.broadcastEmbed(
				DiscordChat.getChannels("discord.channels.deaths"),
				to.getMessages().getFormats(),
				to.getToolTips().getFormats(),
				0x000000
			);
		}

		event.setDeathMessage("");
	}
}
