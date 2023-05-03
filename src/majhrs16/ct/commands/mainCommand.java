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
		api._Sender player = api.new _Sender(sender);
		String lang        = api.getLang(player); 

		if(args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "version":
					api.sendMessage(player, null, plugin.name + " &7Version&f: &a" + plugin.version, lang);
					return true;
	
				case "reload":
					api.sendMessage(player, null, plugin.name + "&7Recargando configuracion&f...", lang);
					plugin.reloadConfig();
					api.sendMessage(player, null, plugin.name + "&7Recargando configuracion&f... &aOK&f.", lang);
					return true;
	
				default:
					api.sendMessage(player, null, plugin.name + "&cEse comando no existe&f!", lang);
					return false;
			}

		} else {
			ArrayList<String> msg = new ArrayList<String>();
			msg.add("&e/ct");
			msg.add("&e  version &a%msg%&f.".replace("%msg%", "Ver version."));
			msg.add("&e  reload &a%msg%&f.".replace("%msg%", "Recargar config."));
			for (int i = 0; i < msg.size(); i++) {
				api.sendMessage(player, null, plugin.name + " " + msg.get(i), lang);
			}
			return true;
		}
	}
}
