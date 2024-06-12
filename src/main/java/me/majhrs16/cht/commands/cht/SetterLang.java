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

		Message.Builder builder = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		switch (args.length) {
//			/cht lang
			case 0:
				API.sendMessage(builder.format("commands.cht.setterLang.getLang", s -> s
					.replace("%lang%", builder.build().getLangTarget().getValue())
				).build());
				break;

// 			/cht lang es
			case 1:
//				Para agregar el comando: /cht lang Majhrs16

				setLang(builder, args[0]);
				break;

//			/cht lang Majhrs16 es
			case 2:
				if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
					API.sendMessage(builder.format("commands.cht.errors.noPermission").build());
					return true; // Para evitar mostrar el unknown command.
				}

				setLangAnother(builder, args[0], args[1]);
				break;

			default:
				API.sendMessage(builder.format("commands.cht.errors.unknown").build());
		}

		return true;
	}

	public void setLang(Message.Builder builder, String lang) {
		if (!API.getTranslator().isSupport(lang)) {
			API.sendMessage(builder.format("commands.cht.setterLang.error.unsupported", s -> s
				.replace("%lang%", lang)
			).build());
			return;
		}

		if (builder.build().getLangTarget().equals(util.convertStringToLang("disabled"))) {
			API.sendMessage(builder.format("commands.cht.setterLang.error.silenced").build());
			return;
		}

		TranslatorBase.LanguagesBase language = util.convertStringToLang(lang);

		API.setLang(builder.build().getSender(), language);

		builder.setLangTarget(language);
		builder.format("commands.cht.setterLang.setLang", s -> s
			.replace("%lang%", language.getValue())
		);

		API.sendMessage(builder.build());
	}

	@SuppressWarnings("deprecation")
	public void setLangAnother(Message.Builder builder, String player, String lang) {
		if (!API.getTranslator().isSupport(lang)) {
			API.sendMessage(builder.format("commands.cht.setterLang.error", s -> s
				.replace("%lang%", lang)
			).build());
			return;
		}

		OfflinePlayer to_player = Bukkit.getOfflinePlayer(player);

		if (!to_player.isOnline() && !to_player.hasPlayedBefore()) {
			API.sendMessage(builder.format("commands.cht.errors.noFoundPlayer", s -> s
					.replace("%player%", player)
			).build());
			return;
		}

		API.setLang(to_player, util.convertStringToLang(lang));

		Message.Builder model = new Message.Builder()
			.format("commands.cht.setterLang.setLangAnother.builder", s -> s
				.replace("%lang%", util.convertStringToLang(lang).getValue())
				.replace("%from_player%", builder.build().getSenderName())
				.replace("%to_player%", to_player.getName())

			).setTo(new Message.Builder()
				.format("commands.cht.setterLang.setLangAnother.to", s -> s
					.replace("%lang%", util.convertStringToLang(lang).getValue())
					.replace("%from_player%", builder.build().getSenderName())
					.replace("%to_player%", to_player.getName())
				)
			);

		API.broadcast(model, BukkitUtils.getOnlinePlayers(), API::sendMessageAsync);
	}
}
