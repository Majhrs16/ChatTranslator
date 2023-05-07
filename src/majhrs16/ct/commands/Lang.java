package majhrs16.ct.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import majhrs16.ct.ChatTranslator;
import majhrs16.ct.util;
import majhrs16.ct.translator.API;

public class Lang implements CommandExecutor {
	private ChatTranslator plugin;
	private API API;

	public Lang(ChatTranslator plugin) {
		this.plugin = plugin;
		this.API    = new API(plugin);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String lang;
		FileConfiguration config  = plugin.getConfig();
		FileConfiguration players = plugin.getPlayers();
		String path               = "";

		try {
			switch (args.length) {
				case 1: // /lang es
					/*
					if (!(sender instanceof Player)) {
						lang = API.GT.getCode(config.getString("default-lang"));
						API.checkSupportLang(lang, "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
						API.sendMessage(sender, "", plugin.name + " &cNo se puede ejecutar este comando desde consola&f.", "es");
						return false;
					}
					*/

					lang = util.assertLang(args[0], "&7El idioma &f'&b%sourceLang%&f' &cno esta soportado&f!.");

					if (sender instanceof Player) {
						players.set(path + ((Player) sender).getUniqueId(), lang);
						plugin.savePlayers();

					} else {
						path = "default-lang";
						config.set(path, lang);
						plugin.saveConfig();
					}

					API.sendMessage(null, sender, "", " &7Su idioma ha sido &aestablecido &7a &b" + lang + "&f.", "es");
					return true;

				case 2:  // /lang Majhrs16 es
					Player player2;

					try {
						player2 = Bukkit.getServer().getPlayer(args[0]);

					} catch (NullPointerException e) {
						API.sendMessage(null, sender, "", "&7El jugador &f'&b" + args[0] + "&f' &cno &7esta &cdisponible&f.", "es");
						player2 = null;
						return false;
					}

					lang = util.assertLang(args[1], "&7El idioma &f'&b%lang%&f' &cno &7esta &csoportado&f!.");

					if (!sender.hasPermission("ChatTranslator.admin")) {
						API.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
						return false;
					}

					players.set(path + player2.getUniqueId(), lang);
					plugin.savePlayers();

					API.broadcast(sender,
						" &f'&b%player%&f' &7ha cambiado el idioma de &f'&b%player2%&f' &7a &b%lang%&f.".replace("%player%", sender.getName()).replace("%player2%", player2.getName()).replace("%lang%", lang), "",
						"es"
					);
					return true;

				default:
					util.assertLang(config.getString("default-lang"), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
					API.sendMessage(null, sender, "", " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f<&6codigo&f>&f.", "es");
					return false;
			}

		} catch (IllegalArgumentException e) {
			sender.sendMessage(API.formatMsg(null, sender, "", e.getMessage(), "es", API.getLang(sender)));
			return false;
		}
	}
}