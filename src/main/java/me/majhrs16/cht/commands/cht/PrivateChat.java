package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.lib.network.translator.TranslatorBase;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class PrivateChat implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (args.length < 2) {
//			/tell <PlayerName> <text>
			return false;
		}

		String player_name      = args[0];
		CommandSender to_player = util.getSenderByName(player_name);

		if (to_player == null) {
			DC.getMessages().setTexts("&cJugador `&F'&b" + player_name + "&f'` &cno encontrado&f!");
			API.sendMessage(DC);
			return true;
		}

		TranslatorBase.LanguagesBase from_lang = API.getLang(sender);
		DC.getMessages().setTexts(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
		DC = util.createChat(sender, DC.getMessages().getTexts(), from_lang, from_lang, "private");

		DC.getTo()
			.setSender(to_player)
			.setLangTarget(API.getLang(to_player));

		API.sendMessage(DC);
		return true;
	}
}