package me.majhrs16.cht.commands.dst;

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

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (Config.TranslateOthers.DISCORD.IF()) {
			if (DC.getSender() instanceof Player) {
				int code = AccountManager.preLink(((Player) DC.getSender()).getUniqueId(), () -> {
					DC.format("commands.discordLinker.timeout");
					API.sendMessage(DC);
				});

				DC.format("commands.discordLinker.done", s -> s
					.replace("%code%", "" + code)
					.replace("%discord_bot_name%", DiscordTranslator.getJDA().getSelfUser().getName())
				);

			} else {
				DC.format("commands.discordLinker.onlyPlayer");
			}

		} else {
			DC.format("commands.discordLinker.activateBot");
		}

		API.sendMessage(DC);
		return true;
	}
}
