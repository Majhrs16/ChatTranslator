package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.network.translator.TranslatorBase;
import me.majhrs16.lib.minecraft.BukkitUtils;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class PrivateChat implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message from = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (args.length < 2) {
			API.sendMessage(from.format("commands.cht.errors.unknown"));
			return true;
		}

		String player_name      = args[0];
		CommandSender to_player = BukkitUtils.getSenderByName(player_name);

		if (to_player == null) {
			API.sendMessage(from.format("commands.cht.errors.noFoundPlayer"));
			return true;
		}

		TranslatorBase.LanguagesBase from_lang = API.getLang(sender);
		from.getMessages().setTexts(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
		from = util.createChat(sender, from.getMessages().getTexts(), from_lang, from_lang, "private");

		from.getTo()
			.setSender(to_player)
			.setLangTarget(API.getLang(to_player));

		API.sendMessage(from);
		return true;
	}
}