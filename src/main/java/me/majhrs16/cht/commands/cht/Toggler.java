package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.minecraft.BukkitUtils;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.events.ChatLimiter;
import me.majhrs16.cht.ChatTranslator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Toggler implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		Message.Builder builder = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			API.sendMessage(builder.format("commands.cht.errors.noPermission").build());
			return true;
		}

		switch (args.length) {
			case 0:
				togglePlugin(builder);
				break;

			case 1:
				togglePlayer(builder, args[0]);
				break;

			default:
				builder.format("commands.cht.errors.unknown");
		}

		API.sendMessage(builder.build());
		return true;
	}

	public void togglePlayer(Message.Builder from, String player) {
		if (player == null)
			throw new NullPointerException("String player is null");

		Player to_player = (Player) BukkitUtils.getSenderByName(player);

		if (to_player == null) {
			API.sendMessage(from.format("commands.cht.noFoundPlayer").build());
			return;
		}

		String[] data = plugin.storage.get(to_player.getUniqueId());
		if (data == null) return;
		String lang = data[2];

		boolean status = lang.startsWith("off-");

		if (status) {
			plugin.storage.set(to_player.getUniqueId(), null, lang.substring(4));

		} else {
			plugin.storage.set(to_player.getUniqueId(), null, "off-" + lang);
		}

		from.format("commands.cht.toggler.togglePlayer." + status, s -> s
			.replace("%to_player%", to_player.getName())
		);
	}
	
	public void togglePlugin(Message.Builder from) {
		ChatLimiter.clear();
		plugin.setDisabled(!plugin.isDisabled());
		from.format("commands.cht.toggler.togglePlugin." + !plugin.isDisabled());
	}
}