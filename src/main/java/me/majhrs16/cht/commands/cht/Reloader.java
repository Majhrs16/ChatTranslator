package me.majhrs16.cht.commands.cht;

import me.majhrs16.cht.exceptions.StorageRegisterFailedException;
import me.majhrs16.cht.util.RunnableWithTriException;
import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.util.cache.internal.Texts;
import me.majhrs16.cht.util.cache.Permissions;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.ChatTranslator;

import me.majhrs16.lib.minecraft.commands.CommandExecutor;
import me.majhrs16.lib.exceptions.ParseYamlException;

import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class Reloader implements CommandExecutor {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean apply(CommandSender sender, String path, String[] args) {
		if (plugin.isDisabled())
			return false;

		Message DC = new Message()
			.setSender(sender)
			.setLangTarget(API.getLang(sender));

		if (!Permissions.ChatTranslator.ADMIN.IF(DC.getSender())) {
			DC.getMessages().setTexts("&cUsted no tiene permisos para ejecutar este comando&f.");
			API.sendMessage(DC);
			return true; // true para evitar mostrar el Unknown command
		}

		DC.getMessages().setTexts("&7Recargando almacenamiento &f...");
		API.sendMessage(DC);

		switch (args.length == 0 ? "all" : args[0].toLowerCase()) {
			case "all":
				reloadAll(DC);
				break;

			case "config":
				reloadConfig(DC);
				break;

			case "commands":
				reloadCommands(DC);
				break;

			case "formats":
				reloadFormats(DC);
				break;

			case "signs":
				reloadSigns(DC);
				break;

			case "storage":
				reloadStorage(DC);
				break;

			default:
				DC.getMessages().setTexts("&7[ &cFAIL &7] &cTipo de almacenamiento no valido&f.");
				API.sendMessage(DC);
				break;
		}

		return true;
	}

	public void reloadAll(Message DC) {
		try {
			reloadFormats(DC);
			reloadConfig(DC);
			reloadCommands(DC);
			reloadSigns(DC);
			reloadStorage(DC);

		} catch (Exception e) {
			if (Permissions.ChatTranslator.ADMIN.IF(DC.getSender()))
				DC.getMessages().setTexts(
					Texts.getString("plugin.title.text") + "&7[&4ERR110&7] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f."
				);

			plugin.logger.error(e.toString());
		}
	}

	private void reload(Message DC, String text, RunnableWithTriException<SQLException, ParseYamlException, StorageRegisterFailedException> action) {
		try {
			action.run();
			DC.getMessages().setFormats("&7[  &aOK  &7] " + text);

		} catch (SQLException | ParseYamlException e) {
			DC.getMessages().setFormats(
				"&7[ &cFAIL &7] " + text,
				"    " + e
			);

		} catch (StorageRegisterFailedException e) {
			DC.getMessages().setFormats("&7[ &cFAIL &7] " + text);
			plugin.logger.error(e.toString());
		}

		API.sendMessage(DC);
	}

	public void reloadConfig(Message DC) {
		reload(DC, "&bconfig&f.&byml", () -> {
			plugin.config.reload();
			plugin.unregisterDiscordBot();
			plugin.registerDiscordBot();
		});
	}

	public void reloadSigns(Message DC) {
		reload(DC, "&bsigns&f.&byml", plugin.signs::reload);
	}

	public void reloadCommands(Message DC) {
		reload(DC, "&bcommands&f.&byml", plugin.commands::reload);
	}

	public void reloadFormats(Message DC) {
		reload(DC, "&bformats&f.&byml", () -> {
			plugin.formats.reload();
			Texts.reload();
		});
	}

	public void reloadStorage(Message DC) {
		switch (plugin.storage.getType()) {
			case "yaml":
				DC.getMessages().setFormats("&b" + plugin.config.get().getString("storage.database") + "&f.&byml");
				break;

			case "sqlite":
				DC.getMessages().setFormats("&bSQLite");
				break;

			case "mysql":
				DC.getMessages().setFormats("&bMySQL");
				break;

			default:
				DC.getMessages().setFormats("&7[&4ERR100&7]"); // En el dado caso que se haya establecido un almacenamiento desconocido y haya pasado el arranque O_o...
				break;
		}

		reload(DC, DC.getMessages().getFormat(0),  plugin.storage::reload);
	}
}