package majhrs16.ct.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util.Updater;
import majhrs16.ct.util.util;

public class MainCommand implements CommandExecutor {
	private ChatTranslator plugin = ChatTranslator.plugin;
	private API API               = new API();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		FileConfiguration config  = plugin.getConfig();
		String lang = API.getLang(sender);

		Message DC = util.getDataConfigDefault();
			DC.setPlayer(sender);
			DC.setLang(lang);

		if (util.IF(config, "debug")) {
			System.out.println("Debug, Name: " + sender.getName());
			System.out.println("Debug, Lang: " + lang);
		}

		if(args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "version":
					if (!plugin.enabled)
						return false;

					DC.setMessages("&7Version&f: &a" + plugin.version);
						API.sendMessage(DC);
					return true;

				case "reload":
					if (!plugin.enabled)
						return false;

					if (!sender.hasPermission("ChatTranslator.admin")) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						return false;
					}

					reloadConfig(DC);
					return true;

				/*
//				Me parece innecesario, CoT puede esto y mas!!

				case "parse":
					if (!plugin.enabled)
						return false;

					if (!sender.hasPermission("ChatTranslator.admin")) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						return false;
					}

					String msgFormat = String.join(" ", args);

					DC.setMessageFormat(msgFormat.replaceFirst("parse", ""));
					DC.setMessages("&eDato de ejemplo");
						API.sendMessage(DC);
					return true;
				*/

				case "lang":
					if (!plugin.enabled)
						return false;

					setLang(sender, args, DC);
					return true;
					
				case "toggle":
					if (!sender.hasPermission("ChatTranslator.admin")) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						return false;
					}

					switch (args.length) {
						case 1:
							TogglePlugin(sender);
							return true;
						
						case 2:
							ToggleOffPlayer(DC, args[1]);
							return true;
							
						default:
							return false;
					}

				case "reset":
					DC.setMessages("&aRestableciendo la config&f...");
						API.sendMessage(DC);

					new Updater().applyCurrentConfig();
					plugin.saveConfig();

					DC.setMessages("&aSe ha restablecido la config exitosamente&f.");
						API.sendMessage(DC);
					return true;

				default:
					if (!plugin.enabled)
						return false;

					DC.setMessages("&7Ese comando &cno &7existe&f!");
						API.sendMessage(DC);
					return false;
			}

		} else {
			if (!plugin.enabled)
				return false;

			new HelperCommand().showToolTip(sender);
			return true;
		}
	}
	
	public void ToggleOffPlayer(Message sender, String player) {
		Player player2;
		try {
			player2 = Bukkit.getServer().getPlayer(player);

		} catch (NullPointerException e) {
			player2 = null;
		}

		if (player2 == null) {
			sender.setMessages("&7El jugador &f'&b" + player + "&f' &cno &7esta &cdisponible&f.");
				API.sendMessage(sender);
			return;
		}

		API.setLang(player2, "disabled");
		plugin.savePlayers();

		sender.setMessages(String.format("&cSe ha desactivado el chat para &f'&b%s&f'&f.", player2.getName()));
			API.sendMessage(sender);
	}
	
	public void TogglePlugin(CommandSender sender) {
		if (plugin.enabled)
			plugin.onDisable();

		else
			plugin.onEnable();

		sender.sendMessage("" + plugin.enabled);
	}

	public Boolean setLang(CommandSender sender, String[] args, Message DC) {
		String lang;
		FileConfiguration config  = plugin.getConfig();
		String path               = "";

		try {
			switch (args.length) {
				case 2: // /ct lang es
					lang = util.assertLang(args[1], "&7El idioma &f'&b" + args[1] + "&f'&c no &7esta soportado&f!.");

					API.setLang(sender, lang);
					plugin.savePlayers();

					DC.setMessages("&7Su idioma ha sido &aestablecido&7 a &b" + lang + "&f.");
					DC.setLang(lang);
						API.sendMessage(DC);
					return true;

				case 3:  // /ct lang Majhrs16 es
					if (!sender.hasPermission("ChatTranslator.admin")) {
						DC.setMessages("&cUsted no tiene permisos para ejecutar este comando&f.");
							API.sendMessage(DC);
						return false;
					}

					Player player2;
					try {
						player2 = Bukkit.getServer().getPlayer(args[1]);

					} catch (NullPointerException e) {
						player2 = null;
					}

					if (player2 == null) {
						DC.setMessages("&7El jugador &f'&b" + args[1] + "&f'&c no &7esta&c disponible&f.");
							API.sendMessage(DC);
						return false;
					}

					lang = util.assertLang(args[2], "&7El idioma &f'&b" + args[2] + "&f'&c no &7esta soportado&f!.");

					API.setLang(player2, lang);
					plugin.savePlayers();

					DC.setMessages(String.format(
						"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b%s&f.",
						sender.getName(),
						player2.getName(),
						lang
					));

					DC.setCancelled(true);

					path = "formats.to";
					Message to_model = new Message(
						DC,
						null,
						config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
						DC.getMessages(),
						config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
						config.contains(path + ".sounds")   ? String.join("\n", config.getStringList(path + ".sounds"))   : null,
						false,

						null,

						util.IF(config, "chat-color-personalized"),
						util.IF(config, "use-PAPI-format")
					);

					API.broadcast(to_model);
					return true;

				default:
					util.assertLang(config.getString("default-lang"), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
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

	public void reloadConfig(Message DC) {
		try {
			plugin.reloadConfig();
			DC.setMessages("&7Recargado &bconfig&f.&byml&f.");
				API.sendMessage(DC);

			plugin.reloadPlayers();
				switch (plugin.getConfig().getString("storage.type").toLowerCase()) {
					case "yaml":
						DC.setMessages("&7Recargado &bplayers&f.&byml&f.");
						break;
	
					case "sqlite":
						DC.setMessages("&7Recargado almacenamiento &bSQLite&f.");
						break;
	
					case "mysql":
						DC.setMessages("&7Recargado almacenamiento &bMySQL&f.");
						break;
				}
			API.sendMessage(DC);

			DC.setMessages("&7Config recargada &aexitosamente&f.");
				API.sendMessage(DC);

		} catch (Exception e) {
			DC.setMessages(plugin.title + "&f [&4ERROR&f] &cNO se pudo recargar la config&f. &ePor favor&f, &evea su consola &f/ &eterminal&f.");
				API.sendMessage(DC);

			e.printStackTrace();
		}
	}
}
