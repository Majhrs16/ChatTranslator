package majhrs16.cht.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.cht.commands.cht.SetterLang;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.commands.cht.Reloader;
import majhrs16.cht.commands.cht.Toggler;
import majhrs16.cht.bool.Permissions;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.Updater;
import majhrs16.lib.BaseLibrary;
import majhrs16.cht.util.util;

public class _CommandHandler implements CommandExecutor {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		FileConfiguration config = plugin.commands.get();

		if (!config.contains(label + ".type"))
			return false;

		String type = config.getString(label + ".type");

		Message DC = util.getDataConfigDefault();
			DC.setSender(sender);
			DC.setLangTarget(API.getLang(sender));

		switch (type.toLowerCase()) {
			case "main":
				if (args.length < 1) {
					showHelp(sender, label, DC);
					return true;

				} else {
					String path = label + "." + args[0];

					if (config.contains(path + ".type")) {
						if (!(type = config.getString(path + ".type")).equals("main"))
							return onCommand(sender, null, path, args);

					} else {
						return unknownCommand(DC);
					}
				}

			case "setterlang":
				return setterLang(sender, DC, args);
	
			case "version":
				return showVersion(DC);
	
			case "reloader":
				return reloader(DC, sender);
	
			case "toggler":
				return toggler(sender, DC, args);
	
			case "reset":
				return reset(DC);
	
			default:
				return unknownCommand(DC);
		}
	}

	public void showHelp(CommandSender sender, String command_base, Message DC) {
		FileConfiguration config = plugin.commands.get();
		String path = command_base;

		String description = String.join("\n", config.getStringList(path + ".toolTips"));
		DC.setToolTips(sender instanceof Player ? description : "	" + description.replace("\n", "\n\t"));

		DC.setMessages(config.getString(path + ".text"));
		API.sendMessage(DC);

		for (String key : config.getConfigurationSection(command_base).getKeys(false)) {
			if (!key.equals("type") && !key.equals("text") && !key.equals("suggest") && !key.equals("toolTip")) {
				path = command_base + "." + key;

				description = String.join("\n", config.getStringList(path + ".toolTips"));
				DC.setToolTips(sender instanceof Player ? description : "	" + description.replace("\n", "\n\t"));

				DC.setMessages(config.getString(path + ".text"));
					API.sendMessage(DC);
			}
		}
	}

	public boolean unknownCommand(Message DC) {
		if (plugin.isDisabled())
			return false;

		DC.setMessages("&7Ese comando &cno &7existe&f!");
			API.sendMessage(DC);
		return false;
	}

	public boolean reset(Message DC) {
		DC.setMessages("&aRestableciendo la config&f...");
			API.sendMessage(DC);
	
		plugin.config.reset();
		new Updater().updateConfig();
	
		DC.setMessages("&aSe ha restablecido la config exitosamente&f.");
			API.sendMessage(DC);
		return true;
	}

	public boolean toggler(CommandSender sender, Message DC, String[] args) {
		if (!Permissions.chattranslator.ADMIN.IF(sender)) {
			DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
				API.sendMessage(DC);
			return false;
		}

		Toggler tog = new Toggler();

		switch (args.length) {
			case 1:
				tog.TogglePlugin(sender);
				return true;

			case 2:
				tog.ToggleOffPlayer(DC, args[1]);
				return true;

			default:
				return false;
		}
	}

	public boolean setterLang(CommandSender sender, Message DC, String[] args) {
		if (plugin.isDisabled())
			return false;
		
		SetterLang setter = new SetterLang();

		try {
			switch (args.length) {
				case 2: // /ct lang es
					setter.setLang(DC, args[1]);
					return true;

				case 3:  // /ct lang Majhrs16 es
					if (!Permissions.chattranslator.ADMIN.IF(sender)) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						return false;
					}

					setter.setLangAnother(DC, args[1], args[2]);
					return true;

				default:
					DC.setMessages("&cSintaxis invalida&f. &aPor favor use la sintaxis&f:\n    &e/ct lang &f[&6player&f] &f<&6codigo&f>&f.");
						API.sendMessage(DC);
					return false;
			}

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				API.sendMessage(DC);
			return false;
		}
	}

	public boolean showVersion(Message DC) {
		if (plugin.isDisabled())
			return false;

		DC.setMessages(plugin.title + plugin.version);
		DC.setToolTips("&7Version del &4kernel&f: &b" + BaseLibrary.version);
			API.sendMessage(DC);
		return true;
	}

	public boolean reloader(Message DC, CommandSender sender) {
		if (plugin.isDisabled())
			return false;

		if (!Permissions.chattranslator.ADMIN.IF(sender)) {
			DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
				API.sendMessage(DC);
			return false;
		}

		new Reloader().reloadAll(DC);
		return true;
	}
}