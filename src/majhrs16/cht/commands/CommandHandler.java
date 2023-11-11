package majhrs16.cht.commands;

import majhrs16.lib.network.translator.GoogleTranslator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import majhrs16.cht.translator.ChatTranslatorAPI;
import majhrs16.lib.storages.ParseYamlException;
import majhrs16.cht.util.updater.ConfigUpdater;
import majhrs16.cht.util.cache.internal.Texts;
import majhrs16.cht.commands.cht.SetterLang;
import majhrs16.cht.util.cache.Permissions;
import majhrs16.cht.events.custom.Message;
import majhrs16.cht.commands.cht.Reloader;
import majhrs16.cht.commands.cht.Toggler;
import majhrs16.dst.utils.AccountManager;
import majhrs16.dst.DiscordTranslator;
import majhrs16.cht.util.cache.Config;
import majhrs16.cht.ChatTranslator;
import majhrs16.cht.util.util;

import org.bukkit.command.Command;
import org.json.simple.JSONObject;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class CommandHandler implements CommandExecutor {
	private ChatTranslator plugin = ChatTranslator.getInstance();
	private ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		FileConfiguration config = plugin.commands.get();

		if (!config.contains(label + ".type"))
			return false;

		String type = config.getString(label + ".type");

		Message DC = new Message();
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
				setterLang(DC, args);
				break;

			case "version":
				showVersion(DC);
				break;

			case "reloader":
				reloader(DC, args);
				break;

			case "toggler":
				toggler(DC, args);
				break;

			case "reset":
				reset(DC);
				break;

			case "linker":
				linker(DC, args);
				break;

			case "private":
				sendPrivateMessage(DC, args);
				break;

			default:
				unknownCommand(DC);
		}

		return true;
	}

	public void sendPrivateMessage(Message DC, String[] args) {
		if (plugin.isDisabled())
			return;

		if (args.length < 3) { // /XD Player HOLA
			unknownCommand(DC);
			return;
		}

		CommandSender from_player = DC.getSender();
		Player to_player = Bukkit.getPlayer(args[1]);

		if (to_player == null) {
			DC.setMessages("&cJugador `&F'&b" + args[1] + "&f'` &cno encontrado&f!");
			DC.setToolTips();

		} else {
			DC.setMessages(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
			DC = util.createChat(from_player, DC.getMessages(), DC.getLangSource(), API.getLang(to_player), "private");

			DC.getTo().setSender(to_player);
			DC.getTo().setLangTarget(API.getLang(to_player));
		}

		API.sendMessage(DC);
	}

	public void linker(Message DC, String[] args) {
		if (Config.TranslateOthers.DISCORD.IF()) {
			switch (args.length) {
				case 1:
					if (DC.getSender() instanceof Player) {
						int code = AccountManager.register(((Player) DC.getSender()).getUniqueId(), () -> {
							DC.setMessages("&cTiempo de espera agotado para su vinculacion de cuenta&f.");
								API.sendMessage(DC);
						});

						DC.setMessages(
							  "&eSu codigo de verificacion es&f: '&b" + code + "&f', &6Y expira en 1 minuto&f.\n"
							+ "    &6Por favor escribalo por privado al bot del servidor &f(&b`" + DiscordTranslator.getJda().getSelfUser().getName() + "`&f)"
						);

					} else {
						DC.setMessages("&cSolo se puede ejecutar este comando en un jugador.");
					}
					break;
			}

		} else {
			DC.setMessages("&cDebe activar `&bconfig." + Config.TranslateOthers.DISCORD.getPath() + "` &cpara usar este comando&f.");
		}

		API.sendMessage(DC);
	}

	@SuppressWarnings("unchecked")
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

		DC.setMessages("`" + text + "`");

		if (sender instanceof Player) {
			DC.setSender(sender);                  // Optimizacion
			DC.setLangTarget(API.getLang(sender)); // Optimizacion

			if (!description.isEmpty())
				DC.setToolTips(description);

			if (util.getMinecraftVersion() >= 7.2) {
				JSONObject jsonMessage = new JSONObject();
				DC = API.formatMessage(DC);

				jsonMessage.put("text", DC.getMessages());

				if (!description.isEmpty()) {
					JSONObject hoverEvent = new JSONObject();
						hoverEvent.put("action", "show_text");
						hoverEvent.put("value", DC.getToolTips());
					jsonMessage.put("hoverEvent", hoverEvent);
				}

				if (suggest != null) {
					DC.setMessages(); // Optimización
					DC.setToolTips("`/" + suggest + "`");

					JSONObject clickEvent = new JSONObject();
						clickEvent.put("action", "suggest_command");
						clickEvent.put("value", API.formatMessage(DC).getToolTips());
					jsonMessage.put("clickEvent", clickEvent);
				}

				DC.setMessagesFormats(jsonMessage.toString());
				DC.setMessages(" "); // Neceario para que funcione el sendMessage
				DC.setToolTips();
			}

		} else {
			DC.setToolTips("\t" + description.replace("\n", "\n\t") + "\n\t");
		}

		API.sendMessage(DC);
	}

	public void showHelp(CommandSender sender, String command_base) {
		FileConfiguration commands = plugin.commands.get();

		sendMessagesAndToolTips(sender, null);
		sendMessagesAndToolTips(sender, command_base);

		for (String key : commands.getConfigurationSection(command_base).getKeys(false)) {
			if (!key.equals("type") && !key.equals("text") && !key.equals("suggest") && !key.equals("toolTips")) {
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
			new ConfigUpdater();
			DC.setMessages("&aSe ha restablecido la config exitosamente&f.");

		} catch (ParseYamlException e) {
			DC.setMessages("&cNO se ha podido restablecer la config&f.");
		}

		API.sendMessage(DC);
	}

	public void toggler(Message DC, String[] args) {
		CommandSender sender = DC.getSender();

		if (!Permissions.ChatTranslator.ADMIN.IF(sender)) {
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

	public void setterLang(Message DC, String[] args) {
		if (plugin.isDisabled())
			return;

		SetterLang setter = new SetterLang();

		try {
			switch (args.length) {
				case 1: // /XD lang
					DC.setMessages("&aSu idioma establecido es&f: &b" + GoogleTranslator.Languages.valueOf(API.getLang(DC.getSender()).toUpperCase()).getValue() + "&f.");
						API.sendMessage(DC);
					break;

				case 2: // /XD lang es
					setter.setLang(DC, args[1]);
					break;

				case 3:  // /XD lang Majhrs16 es
					if (!Permissions.ChatTranslator.ADMIN.IF(DC.getSender())) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						break;
					}

					setter.setLangAnother(DC, args[1], args[2]);
					break;

				default:
					DC.setMessages("&cSintaxis invalida&f. &aPor favor use la sintaxis&f:\n    &e/cht lang &f[&6player&f] &f<&6codigo&f>&f.");
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

		DC.setMessages(Texts.get("commands.main.version.text"));
		DC.setToolTips(Texts.get("commands.main.version.toolTips"));
			API.sendMessage(DC);
	}

	public void reloader(Message DC, String[] args) {
		if (plugin.isDisabled())
			return;

		if (!Permissions.ChatTranslator.ADMIN.IF(DC.getSender())) {
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
					reloader.reloadStorage();
					break;

				default:
					unknownCommand(DC);
			}
		}
	}
}