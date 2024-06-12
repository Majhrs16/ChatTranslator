package me.majhrs16.cht.commands.dst;

import me.majhrs16.cht.util.util;
import me.majhrs16.lib.minecraft.commands.CommandExecutor;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;

import me.majhrs16.dst.utils.AccountManager;
import me.majhrs16.dst.DiscordTranslator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordLinker implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message.Builder builder = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (Config.TranslateOthers.DISCORD.IF() && DiscordTranslator.getJDA() != null) {
			if (sender instanceof Player) {
				int code = AccountManager.preLink(util.getUUID(sender), () -> {
					builder.format("commands.cht.discordLinker.timeout");
					API.sendMessage(builder.build());
				});

				builder.format("commands.cht.discordLinker.done", s -> s
					.replace("%code%", "" + code)
					.replace("%discord_bot_name%", DiscordTranslator.getJDA().getSelfUser().getName())
				);

			} else {
				builder.format("commands.cht.discordLinker.onlyPlayer");
			}

		} else {
			builder.format("commands.cht.discordLinker.activateBot");
		}

		API.sendMessage(builder.build());
		return true;
	}
}