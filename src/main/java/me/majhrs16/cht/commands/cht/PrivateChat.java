package me.majhrs16.cht.commands.cht;

import me.majhrs16.cht.events.custom.Formats;
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

		Message.Builder builder = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (args.length < 2) {
			API.sendMessage(builder.format("commands.cht.errors.unknown").build());
			return true;
		}

		String player_name      = args[0];
		CommandSender to_player = BukkitUtils.getSenderByName(player_name);

		if (to_player == null) {
			API.sendMessage(builder.format("commands.cht.errors.noFoundPlayer").build());
			return true;
		}

		TranslatorBase.LanguagesBase from_lang = API.getLang(sender);
		builder = util.createChat(
			sender,
			new String[] { String.join(" ", Arrays.copyOfRange(args, 1, args.length)) },
			from_lang,
			from_lang,
			"private"
		);

		builder.setTo(builder.build().clone()
			.setSender(to_player)
			.setLangTarget(API.getLang(to_player))
		);

		API.sendMessage(builder.build());
		return true;
	}
}