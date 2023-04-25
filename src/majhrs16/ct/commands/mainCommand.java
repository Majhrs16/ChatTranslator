package majhrs16.ct.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import majhrs16.ct.api;
import majhrs16.ct.main;

public class mainCommand implements CommandExecutor {
	private main plugin;
	private api api;
	public mainCommand(main plugin) {
		this.plugin = plugin;
		this.api    = new api(plugin);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String lang = api.getLang(sender);

		if(args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "version":
					showVersion(sender, lang);
					return true;
	
				case "reload":
					if (!sender.hasPermission("ChatTranslator.admin")) {
						api.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
						return false;
					}

					updateConfig(sender, lang);
					return true;
	
				default:
					api.sendMessage(null, sender, "", plugin.name + "&7Ese comando &cno existe&f!", lang);
					return false;
			}

		} else {
			ArrayList<String> msg = new ArrayList<String>();
			msg.add("&e/ct");
			msg.add("&e  version &a%msg%&f.".replace("%msg%", "Ver version."));
			msg.add("&e  reload &a%msg%&f.".replace("%msg%", "Recargar config."));
			for (int i = 0; i < msg.size(); i++) {
				api.sendMessage(null, sender, "", plugin.name + " " + msg.get(i), lang);
			}
			return true;
		}
	}
	
	public void showVersion(CommandSender sender, String lang) {
		api.sendMessage(null, sender, "", plugin.name + " &7Version&f: &a" + plugin.version, lang); 
	}
	
	public void updateConfig(CommandSender sender, String lang) {
		api.sendMessage(null, sender, "", plugin.name + "&7Recargando configuracion&f...", lang);
		plugin.reloadConfig();
		api.sendMessage(null, sender, "", plugin.name + "&7Recargando configuracion&f... &aOK&f.", lang);
	}
}
