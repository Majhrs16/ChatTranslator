package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

public class SetterLang implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message from = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		switch (args.length) {
			case 0: // /cht lang
				API.sendMessage(from.format("commands.setterLang.getLang", s -> s
					.replace("%lang%", from.getLangTarget().getValue())
				));
				break;

			case 1: // /cht lang es
				// agregar el comando: /cht lang Majhrs16

				setLang(from, args[0]);
				break;

			case 2:  // /cht lang Majhrs16 es
				if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
					API.sendMessage(from.format("commands.errors.noPermission"));
					return true; // Para evitar mostrar el unknown command.
				}

				setLangAnother(from, args[0], args[1]);
				break;

			default:
				API.sendMessage(from.format("commands.errors.unknown"));
		}

		return true;
	}

	public void setLang(Message from, String lang) {
		if (!API.getTranslator().isSupport(lang)) {
			API.sendMessage(from.format("commands.setterLang.setLang.error", s -> s
				.replace("%lang%", lang)
			));
			return;
		}

		TranslatorBase.LanguagesBase language = util.convertStringToLang(lang);

		API.setLang(from.getSender(), language);

		from.setLangTarget(language);
		from.format("commands.setterLang.setLang.done", s -> s
			.replace("%lang%", language.getValue())
		);

		API.sendMessage(from);
	}

	@SuppressWarnings("deprecation")
	public void setLangAnother(Message from, String player, String lang) {
		if (!API.getTranslator().isSupport(lang)) {
			API.sendMessage(from.format("commands.setterLang.setLang.error", s -> s
				.replace("%lang%", lang)
			));
			return;
		}

		OfflinePlayer to_player;
		try {
			to_player = Bukkit.getOfflinePlayer(player);

			if (!to_player.isOnline() && !to_player.hasPlayedBefore())
				throw new NullPointerException();

		} catch (NullPointerException e) {
			API.sendMessage(from.format("commands.errors.noFoundPlayer", s -> s
				.replace("%player%", player)
			));
			return;
		}

		API.setLang(to_player, util.convertStringToLang(lang));

		Message model = new Message();
			model.format("commands.setterLang.setLangAnother.done.from", s -> s
				.replace("%lang%", util.convertStringToLang(lang).getValue())
				.replace("%from_player%", from.getSenderName())
				.replace("%to_player%", to_player.getName())
			);

		Message to_model = model.clone();
			to_model.format("commands.setterLang.setLangAnother.done.to", s -> s
				.replace("%lang%", util.convertStringToLang(lang).getValue())
				.replace("%from_player%", from.getSenderName())
				.replace("%to_player%", to_player.getName())
			);
		model.setTo(to_model);

		API.broadcast(model, BukkitUtils.getOnlinePlayers(), API::broadcast);
	}
}