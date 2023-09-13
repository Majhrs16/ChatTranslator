package majhrs16.cht.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import majhrs16.lib.storages.YAML.ParseYamlException;
import majhrs16.cht.translator.ChatTranslatorAPI;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.commands.cht.SetterLang;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.commands.cht.Reloader;
import majhrs16.cht.commands.cht.Toggler;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.Updater;
import majhrs16.cht.util.util;

public class CommandHandler implements CommandExecutor {
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

		if (!label.contains(".") && !type.equals("main")) { // En caso de ser un comando raiz.
			String[] args_fix = new String[args.length + 1];
			args_fix[0] = "XD"; // Preparar el args en estos casos para asegurar una estructura fija y asi funcionar el sistema de comandos custom.

			for (int i = 0; i < args.length; i++)
				args_fix[i + 1] = args[i]; // Restaurar el resto de args.

			args = args_fix;
		}

		switch (type.toLowerCase()) {
			case "main":
				if (args.length < 1) {
					showHelp(sender, label);

				} else {
					String path = label + "." + args[0];

					if (config.contains(path + ".type")) {
						if (!(type = config.getString(path + ".type").toLowerCase()).equals("main")) {
							onCommand(sender, null, path, args);
						}

					} else {
						unknownCommand(DC);
					}
				}

				break;

			case "setterlang":
				setterLang(sender, DC, args);
				break;

			case "version":
				showVersion(DC);
				break;

			case "reloader":
				reloader(DC, sender, args);
				break;

			case "toggler":
				toggler(sender, DC, args);
				break;

			case "reset":
				reset(DC);
				break;

			default:
				unknownCommand(DC);
		}

		return true;
	}

	private void sendMessagesAndToolTips(CommandSender sender, String path) {
		FileConfiguration commands = plugin.commands.get();

		if (path == null)
			path = "";

		else
			path = path + ".";

		String text = commands.getString(path + "text");
		if (text == null) return;

		String description = String.join("\n", commands.getStringList(path + "toolTips"));
		String suggest = commands.getString(path + "suggest");

		Message DC = util.getDataConfigDefault();

		if (sender instanceof Player) {
			DC.setSender(sender);                  // Optimizacion
			DC.setLangTarget(API.getLang(sender)); // Optimizacion

			DC.setMessages("`" + text + "`");
			if (!description.isEmpty())
				DC.setToolTips(description);
			DC = API.formatMessage(DC);

			TextComponent message = new TextComponent(DC.getMessages());
				if (!description.isEmpty()) {
					@SuppressWarnings("deprecation")
					HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(DC.getToolTips()).create());
						message.setHoverEvent(hoverEvent);
				}

				if (suggest != null) {
					DC.setMessages(null); // Optimizacion
					DC.setToolTips("`/" + suggest + "`");
					message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, API.formatMessage(DC).getToolTips()));
				}

			((Player) DC.getSender()).spigot().sendMessage(message);

		} else {
			DC.setMessages(text);
			if (!description.isEmpty())
				DC.setToolTips("\t" + description.replace("\n", "\n\t") + "\n\t");

			API.sendMessage(DC);
		}
	}

	public void showHelp(CommandSender sender, String command_base) {
		FileConfiguration commands = plugin.commands.get();

		sendMessagesAndToolTips(sender, null);
		sendMessagesAndToolTips(sender, command_base);

		for (String key : commands.getConfigurationSection(command_base).getKeys(false)) {
			if (!key.equals("type") && !key.equals("text") && !key.equals("suggest") && !key.equals("toolTip")) {
				sendMessagesAndToolTips(sender, command_base + "." + key);
			}
		}
	}

	public void unknownCommand(Message DC) {
		if (plugin.isDisabled())
			return;

		DC.setMessages("&7Ese comando &cno &7existe&f!");
			API.sendMessage(DC);
	}

	public void reset(Message DC) {
		DC.setMessages("&aRestableciendo la config&f...");
			API.sendMessage(DC);

		try {
			plugin.config.reset();
			new Updater().updateConfig();
			DC.setMessages("&aSe ha restablecido la config exitosamente&f.");

		} catch (ParseYamlException e) {
			DC.setMessages("&cNO se ha podido restablecer la config&f.");
		}

		API.sendMessage(DC);
	}

	public void toggler(CommandSender sender, Message DC, String[] args) {
		if (!Permissions.chattranslator.ADMIN.IF(sender)) {
			DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
				API.sendMessage(DC);
			return;
		}

		Toggler tog = new Toggler();

		switch (args.length) {
			case 1:
				tog.TogglePlugin(sender);
				break;

			case 2:
				tog.ToggleOffPlayer(DC, args[1]);
				break;
		}
	}

	public void setterLang(CommandSender sender, Message DC, String[] args) {
		if (plugin.isDisabled())
			return;

		SetterLang setter = new SetterLang();

		try {
			switch (args.length) {
				case 2: // /XD lang es
					setter.setLang(DC, args[1]);
					break;

				case 3:  // /XD lang Majhrs16 es
					if (!Permissions.chattranslator.ADMIN.IF(sender)) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						break;
					}

					setter.setLangAnother(DC, args[1], args[2]);
					break;

				default:
					DC.setMessages("&cSintaxis invalida&f. &aPor favor use la sintaxis&f:\n    &e/ct lang &f[&6player&f] &f<&6codigo&f>&f.");
						API.sendMessage(DC);
			}

		} catch (IllegalArgumentException e) {
			DC.setMessages(e.getMessage());
				API.sendMessage(DC);
		}
	}

	public void showVersion(Message DC) {
		if (plugin.isDisabled())
			return;

		DC.setMessages(Texts.COMMANDS.MAIN.VERSION.TEXT);
		DC.setToolTips(Texts.COMMANDS.MAIN.VERSION.TOOLTIPS);
			API.sendMessage(DC);
	}

	public void reloader(Message DC, CommandSender sender, String[] args) {
		if (plugin.isDisabled())
			return;

		if (!Permissions.chattranslator.ADMIN.IF(sender)) {
			DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
				API.sendMessage(DC);
			return;
		}

		Reloader reloader = new Reloader(DC);

		if (args.length <= 1) {
			reloader.reloadAll();

		} else {
			switch (args[1].toLowerCase()) {
				case "config":
					reloader.reloadConfig();
					break;

				case "commands":
					reloader.reloadCommands();
					break;

				case "messages":
					reloader.reloadMessages();
					break;

				case "signs":
					reloader.reloadSigns();
					break;

				case "storage":
					reloader.reloadPlayers();
					break;

				default:
					unknownCommand(DC);
			}
		}
	}
}