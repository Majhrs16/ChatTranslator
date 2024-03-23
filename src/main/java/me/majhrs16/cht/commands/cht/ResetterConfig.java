package me.majhrs16.cht.commands.cht;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.exceptions.ParseYamlException;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.updater.ConfigUpdater;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;

import org.bukkit.command.CommandSender;

public class ResetterConfig implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();

	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		DC.format("commands.resetterConfig");
		API.sendMessage(DC);

		try {
			plugin.config.reset();
			new ConfigUpdater();
			new Reloader().reloadAll(DC);
			DC.format("commands.resetterConfig.done");

		} catch (ParseYamlException e) {
			DC.format("commands.resetterConfig.error");
		}

		API.sendMessage(DC);
		return true;
	}
}
