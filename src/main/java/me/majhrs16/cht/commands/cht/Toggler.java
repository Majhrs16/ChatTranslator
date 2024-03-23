package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Toggler implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			DC.format("commands.noPermission");
			API.sendMessage(DC);
			return true; // Para evitar mostrar el unknown command.
		}

		switch (args.length) {
			case 0:
				TogglePlugin(DC);
				break;

			case 1:
				ToggleOffPlayer(DC, args[0]);
				break;
		}

		API.sendMessage(DC);
		return true;
	}
	
	public void ToggleOffPlayer(Message from, String player) {
		if (player == null)
			throw new NullPointerException("String player is null");

		Player to_player = (Player) util.getSenderByName(player);

		if (to_player == null) {
			API.sendMessage(from.format("commands.noFoundPlayer"));
			return;
		}

		API.setLang(to_player, util.convertStringToLang("disabled"));

		from.format("commands.toggler.toggleoffPlayer", s -> s
			.replace("%to_player%", to_player.getName())
		);
	}
	
	public void TogglePlugin(Message from) {
		ChatLimiter.clear();
		plugin.setDisabled(!plugin.isDisabled());
		from.format("commands.toggler.togglePlugin." + !plugin.isDisabled());
	}
}