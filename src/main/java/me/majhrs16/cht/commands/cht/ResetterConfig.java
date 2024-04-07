package me.majhrs16.cht.commands.cht;

import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.util.updater.CommandsUpdater;
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
		Message from = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			API.sendMessage(from.format("commands.errors.noPermission"));
			return true; // Para evitar mostrar el unknown command.
		}

		from.format("commands.resetterConfig");
		API.sendMessage(from);

		try {
			plugin.config.reset();
			plugin.formats.reset();
			plugin.commands.reset();

			new ConfigUpdater();
			new CommandsUpdater();
			new Reloader().reloadAll(from);
			from.format("commands.resetterConfig.done");

		} catch (ParseYamlException e) {
			from.format("commands.resetterConfig.error");
		}

		API.sendMessage(from);
		return true;
	}
}
