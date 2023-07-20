package majhrs16.ct.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.events.custom.Message;
import majhrs16.ct.translator.API.API;
import majhrs16.ct.util.Updater;
import majhrs16.ct.util.util;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CT implements CommandExecutor {
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

					showVersion(DC);
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

					if (args.length == 2) {
						Player player2;
						try {
							player2 = Bukkit.getServer().getPlayer(args[1]);

						} catch (NullPointerException e) {
							player2 = null;
						}

						if (player2 == null) {
							DC.setMessages("&7El jugador &f'&b" + args[1] + "&f' &cno &7esta &cdisponible&f.");
								API.sendMessage(DC);
							return false;
						}

						API.setLang(player2, "disabled");

						DC.setMessages(String.format("&cSe ha desactivado el chat para &f'&b%s&f'&f.", player2.getName()));
							API.sendMessage(DC);

					} else if (args.length == 1) {
						plugin.enabled = !plugin.enabled;
						majhrs16.ct.util.ChatLimiter.chat.clear();
						sender.sendMessage("" + plugin.enabled);
					}
					return true;

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

			showHelp(sender);
			return true;
		}
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
					plugin.saveConfig();

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
					plugin.saveConfig();

					DC.setMessages(String.format(
						"&f'&b%s&f' &7ha cambiado el idioma de &f'&b%s&f'&7 a &b%s&f.",
						sender.getName(),
						player2.getName(),
						lang
					));

					DC.setShow(false);

					path = "formats.to";
					Message to_model = new Message(
						DC,
						null,
						config.contains(path + ".messages") ? String.join("\n", config.getStringList(path + ".messages")) : null,
						DC.getMessages(),
						config.contains(path + ".toolTips") ? String.join("\n", config.getStringList(path + ".toolTips")) : null,
						config.contains(path + ".sounds")   ? String.join("\n", config.getStringList(path + ".sounds"))   : null,
						true,

						null,

						util.IF(config, "chat-color-personalized"),
						util.IF(config, "use-PAPI-format")
					);

					API.broadcast(DC, to_model);
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

	public void showHelp(CommandSender sender) {
		ArrayList<String> msg = new ArrayList<String>();
			msg.add(plugin.title + "\n&aTraduce tu chat de Minecraft a cualquier idioma&f!!");
			msg.add("&e  /ct");
			msg.add("&e    lang &f[&6Jugador&f] &f<&6codigo&f>\n"
				+ "&7Especifique con su codigo de idioma&f, &apara traducir el chat a su gusto&f.\n"
				+ "&f    (&7Independientemente de su lenguaje en el Minecraft&f)\n"
				+ "\n"
				+ "&aTrucos&f:\n"
				+ "&7  Puede poner &bauto &7como codigo para volver a la"
				+ "&7    deteccion automatica del idioma de su Minecraft&f."
				+ "\n"
				+ "&7  Puede poner &boff &7como codigo para &cdeshabilitar &7la"
				+ "&7    traduccion automatica para el jugador especificado&f."
			);
			msg.add("&e    parse &f<&eformatMsg&f>\n&aProcesa en tiempo real formatMsg&f(&7Sirve para testear &f;&aD&f)&f.");
			msg.add("&e    version\n&aVisualizar version&f.");
			msg.add("&e    reload\n&aRecargar config&f.");
			msg.add("&e    toggle &f[&6Jugador&f]\n&aActiva o desactiva el chat para el jugador o por defecto en global&f.\n&e    Advertencia&f: &eEste comando limpia los mensajes pendientes del chat&f.");
			msg.add("&e    reset\n&4Restablece la config&f,&e Pero no los datos de lenguajes&f.");
		showToolTip(sender, msg);
	}

	public void showToolTip(CommandSender sender, ArrayList<String> msg) {
		for (int i = 0; i < msg.size(); i++) {
			if (msg.get(i) == "") {
				sender.sendMessage("");
				continue;
			}

			String[] l         = msg.get(i).split("\n", 2);
			String title       = ChatColor.translateAlternateColorCodes("&".charAt(0), l[0]);
			String description = "";

			if (l.length > 1)
				description = API.formatMsg(null, sender, "$ct_messages$", l[1], "es", API.getLang(sender), true, true);

			if (sender instanceof Player) {
				Player p              = (Player) sender;
			    TextComponent message = new TextComponent(title);

				if (l.length > 1) {
					ComponentBuilder hoverText = new ComponentBuilder(description);
					message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create()));
				}

				/* message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/comando")); // Habria q hacer parentes de comandos, mucho lio... */
			    p.spigot().sendMessage(message);

			} else {
				Bukkit.getConsoleSender().sendMessage(title);
				if (l.length > 1)
					Bukkit.getConsoleSender().sendMessage("\t" + description);
			}
		}
	}

	public void showVersion(Message DC) {
		DC.setMessages("&7Version&f: &a" + plugin.version);
			API.sendMessage(DC);
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
			DC.setMessages("&f [&4ERROR&f] &cNO se pudo recargar la config&f. &ePor favor, vea su consola &f/ &eterminal&f.");
				API.sendMessage(DC);
			e.printStackTrace();
		}
	}
}
