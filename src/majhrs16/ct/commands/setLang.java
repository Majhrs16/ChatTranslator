package majhrs16.ct.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import majhrs16.ct.main;
import majhrs16.ct.api;

public class setLang implements CommandExecutor {
	private main plugin;
	private api api;

	public setLang(main plugin) {
		this.plugin = plugin;
		this.api    = new api(plugin);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String lang;
		FileConfiguration config  = plugin.getConfig();
		FileConfiguration players = plugin.getPlayers(); // config;
		String path               = "";

		try {
			switch (args.length) {
				case 1:
					/*
					if (!(sender instanceof Player)) {
						lang = api.GT.getCode(config.getString("default-lang"));
						api.checkSupportLang(lang, "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
						api.sendMessage(sender, "", plugin.name + " &cNo se puede ejecutar este comando desde consola&f.", "es");
						return false;
					}
					*/

					lang = api.GT.getCode(args[0]);
					api.checkSupportLang(lang, "&7El idioma &f'&b%lang%&f' &cno esta soportado&f!.");

					if (sender instanceof Player) {
						players.set(path + ((Player) sender).getUniqueId(), lang);
						plugin.savePlayers();

					} else {
						path = "default-lang";
						config.set(path, lang);
						plugin.saveConfig();
					}

					api.sendMessage(null, sender, "", plugin.name + " &7Su idioma ha sido &aestablecido &7a &b" + lang + "&f.", "es");
					return true;
				
				case 2:
					Player player2;

					try {
						player2 = Bukkit.getServer().getPlayer(args[0]);

					} catch (NullPointerException e) {
						api.sendMessage(null, sender, "", "&7El jugador &f'&b" + args[0] + "&f' &cno &7esta &cdisponible&f.", "es");
						player2 = null;
						return false;
					}
	
					lang = args[1];
					api.checkSupportLang(lang, "&7El idioma &f'&b%lang%&f' &cno &7esta &csoportado&f!.");
					lang = api.GT.getCode(lang);

					if (!sender.hasPermission("ChatTranslator.admin")) {
						api.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
						return false;
					}

					players.set(path + player2.getUniqueId(), lang);
					plugin.savePlayers();

					api.broadcast(sender,
						plugin.name + " &f'&b%player%&f' &7ha cambiado el idioma de &f'&b%player2%&f' &7a &b%lang%&f.".replace("%player%", sender.getName()).replace("%player2%", player2.getName()).replace("%lang%", lang), "",
						"es"
					);
					return true;

				default:
					api.checkSupportLang(config.getString("default-lang"), "&7El idioma por defecto &f'&b%lang%&f' &cno esta soportado&f!.");
					api.sendMessage(null, sender, "", plugin.name + " &cSintaxis invalida&f. &aPor favor use la sintaxis&f: &e/lang &f[&6codigo&f]&f.", "es");
					return false;
			}

		} catch (IllegalArgumentException e) {
			sender.sendMessage(api.formatMsg(null, sender, "", plugin.name + " " + e.getMessage(), "es", api.getLang(sender)));
			return false;
		}
	}
}