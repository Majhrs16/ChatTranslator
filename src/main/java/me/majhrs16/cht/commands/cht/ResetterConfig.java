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
		Message.Builder builder = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			API.sendMessage(builder.format("commands.cht.errors.noPermission").build());

//			Evitar mostrar el clasico unknown command.
			return true;
		}

		API.sendMessage(builder.format("commands.cht.resetterConfig").build());

		try {
			plugin.config.reset();
			plugin.formats.reset();
			plugin.commands.reset();

			new ConfigUpdater();
			new CommandsUpdater();
			new Reloader().reloadAll(builder);
			builder.format("commands.cht.resetterConfig.done");

		} catch (ParseYamlException e) {
			builder.format("commands.cht.resetterConfig.error");
		}

		API.sendMessage(builder.build());
		return true;
	}
}
