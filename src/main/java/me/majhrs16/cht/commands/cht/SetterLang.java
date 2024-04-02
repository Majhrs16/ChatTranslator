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

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		try {
			switch (args.length) {
				case 0: // /cht lang
					DC.format("commands.setterLang.getLang");
					API.sendMessage(DC);
					break;

				case 1: // /cht lang es
					// agregar el comando: /cht lang Majhrs16

					setLang(DC, args[0]);
					break;

				case 2:  // /cht lang Majhrs16 es
					if (!Permissions.ChatTranslator.ADMIN.IF(DC.getSender())) {
						DC.format("commands.noPermission");
						API.sendMessage(DC);
						break;
					}

					setLangAnother(DC, args[0], args[1]);
					break;

				default:
					DC.format("commands.setterLang.error");
					API.sendMessage(DC);
			}

		} catch (IllegalArgumentException e) {
			DC.getMessages().setTexts(e.getMessage());
			API.sendMessage(DC);
		}

		return true;
	}

	public void setLang(Message DC, String lang) {
		if (!API.getTranslator().isSupport(lang)) {
			DC.format("commands.setterLang.setLang.error", null, s -> s
				.replace("%lang%", lang)
			);
			API.sendMessage(DC);
			return;
		}

		TranslatorBase.LanguagesBase language = util.convertStringToLang(lang);

		API.setLang(DC.getSender(), language);

		DC.setLangTarget(language);
		DC.format("commands.setterLang.setLang.done");

		API.sendMessage(DC);
	}

	@SuppressWarnings("deprecation")
	public void setLangAnother(Message DC, String player, String lang) {
		if (!API.getTranslator().isSupport(lang)) {
			API.sendMessage(DC.format("commands.setterLang.setLang.error", null, s -> s
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
			API.sendMessage(DC.format("commands.noFoundPlayer"));
			return;
		}

		API.setLang(to_player, util.convertStringToLang(lang));

		Message model = new Message();
			model.format("commands.setterLang.setLangAnother.done.from", null, s -> s
				.replace("%lang%", util.convertStringToLang(lang).getValue())
				.replace("%from_player%", DC.getSenderName())
				.replace("%to_player%", to_player.getName())
			);

		Message to_model = model.clone();
			to_model.format("commands.setterLang.setLangAnother.done.to", null, s -> s
				.replace("%lang%", util.convertStringToLang(lang).getValue())
				.replace("%from_player%", DC.getSenderName())
				.replace("%to_player%", to_player.getName())
			);
		model.setTo(to_model);

		API.broadcast(model, BukkitUtils.getOnlinePlayers(), API::broadcast);
	}
}