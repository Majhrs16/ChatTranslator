package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.minecraft.BukkitUtils;

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
		Message from = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			API.sendMessage(from.format("commands.errors.noPermission"));
			return true; // Para evitar mostrar el unknown command.
		}

		switch (args.length) {
			case 0:
				togglePlugin(from);
				break;

			case 1:
				toggleOffPlayer(from, args[0]);
				break;

			default:
				from.format("commands.errors.unknown");
		}

		API.sendMessage(from);
		return true;
	}
	
	public void toggleOffPlayer(Message from, String player) {
		if (player == null)
			throw new NullPointerException("String player is null");

		Player to_player = (Player) BukkitUtils.getSenderByName(player);

		if (to_player == null) {
			API.sendMessage(from.format("commands.noFoundPlayer"));
			return;
		}

		API.setLang(to_player, util.convertStringToLang("disabled"));

		from.format("commands.toggler.toggleoffPlayer", s -> s
			.replace("%to_player%", to_player.getName())
		);
	}
	
	public void togglePlugin(Message from) {
		ChatLimiter.clear();
		plugin.setDisabled(!plugin.isDisabled());
		from.format("commands.toggler.togglePlugin." + !plugin.isDisabled());
	}
}