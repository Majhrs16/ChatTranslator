package me.majhrs16.cht.commands.cht;

import me.majhrs16.cht.exceptions.StorageRegisterFailedException;
import me.majhrs16.cht.util.RunnableWithTriException;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.updater.CommandsUpdater;
import me.majhrs16.cht.util.updater.FormatsUpdater;
import me.majhrs16.cht.util.updater.ConfigUpdater;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.storages.YAML;

import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class Reloader implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message.Builder builder = new Message.Builder()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
			API.sendMessage(builder.format("commands.cht.errors.noPermission").build());
			return true; // Para evitar mostrar el unknown command.
		}

		builder.format("commands.cht.reloader");
		API.sendMessage(builder.build());

		plugin.setDisabled(true);

		switch (args.length == 0 ? "all" : args[0].toLowerCase()) {
			case "all":
				reloadAll(builder);
				break;

			case "formats":
				reloadFormats(builder);
				break;

			case "config":
				reloadConfig(builder);
				break;

			case "commands":
				reloadCommands(builder);
				break;

			case "signs":
				reloadSigns(builder);
				break;

			case "storage":
				reloadStorage(builder);
				break;

			default:
				API.sendMessage(builder.format("commands.cht.errors.unknown").build());
				break;
		}

		plugin.setDisabled(false);

		return true;
	}

	public void reloadAll(Message.Builder builder) {
		try {
			reloadFormats(builder);
			reloadConfig(builder);
			reloadCommands(builder);
			reloadSigns(builder);
			reloadStorage(builder);

		} catch (Exception e) {
			plugin.logger.error(e.toString());
			if (Permissions.ChatTranslator.ADMIN.IF(builder.build().getSender()))
				API.sendMessage(builder.format("commands.cht.reloader.error.fatal").build());
		}
	}

	private void reload(Message.Builder builder, String text, RunnableWithTriException<SQLException, ParseYamlException, StorageRegisterFailedException> action) {
		try {
			action.run();

			builder.format("commands.cht.reloader.done", s -> s
				.replace("%file%", text)
			);

		} catch (SQLException | ParseYamlException | StorageRegisterFailedException e) {
			builder.format("commands.cht.reloader.error.file", s -> s
				.replace("%file%", text)
				.replace("%reason%", e.toString())
			);
		}

		API.sendMessage(builder.build());
	}

	public void reloadFormats(Message.Builder builder) {
		reload(builder, "&bformats.yml", () -> {
			YAML yaml = plugin.formats;
			boolean rescue = yaml.isReadonly();

			if (rescue) {
				String folder  = plugin.getDataFolder().getPath();
				yaml = new YAML(folder, "formats.yml");
			}

			yaml.reload();
			plugin.formats = yaml;

			if (rescue) new FormatsUpdater();

			Texts.reload();
		});
	}

	public void reloadConfig(Message.Builder builder) {
		reload(builder, "&bconfig.yml", () -> {
			YAML yaml = plugin.config;
			boolean rescue = yaml.isReadonly();

			if (rescue) {
				String folder  = plugin.getDataFolder().getPath();
				yaml = new YAML(folder, "config.yml");
			}

			yaml.reload();
			plugin.config = yaml;

			if (rescue) new ConfigUpdater();

			plugin.unregisterDiscordBot();
			plugin.registerDiscordBot();
		});
	}

	public void reloadCommands(Message.Builder builder) {
		reload(builder, "&bcommands.yml", () -> {
			YAML yaml = plugin.commands;
			boolean rescue = yaml.isReadonly();

			if (rescue) {
				String folder  = plugin.getDataFolder().getPath();
				yaml = new YAML(folder, "commands.yml");
			}

			yaml.reload();
			plugin.commands = yaml;

			if (rescue) new CommandsUpdater();
		});
	}

	public void reloadSigns(Message.Builder builder) {
		reload(builder, "&bsigns.yml", () -> {
			YAML yaml = plugin.signs;
			boolean rescue = yaml.isReadonly();

			if (rescue) {
				String folder  = plugin.getDataFolder().getPath();
				yaml = new YAML(folder, "signs.yml");
			}

			yaml.reload();
			plugin.signs = yaml;
		});
	}

	public void reloadStorage(Message.Builder builder) {
		String text;

		switch (plugin.storage.getType()) {
			case "yaml":
				text ="&b" + plugin.config.get().getString("storage.database") + ".yml";
				break;

			case "sqlite":
				text = "&bSQLite";
				break;

			case "mysql":
				text = "&bMySQL";
				break;

			default:
//				En el dado caso que se haya establecido un almacenamiento desconocido y haya pasado el arranque O_o.
				text = "&9???";
				break;
		}

		reload(builder, text,  plugin.storage::reload);
	}
}