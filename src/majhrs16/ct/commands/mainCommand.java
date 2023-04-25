package majhrs16.ct.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import majhrs16.ct.api;
import majhrs16.ct.main;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
			msg.add(plugin.name);
			msg.add("&e  /ct");
			msg.add("&e    version\n&a  Ver version&f.");
			msg.add("&e    reload\n&a  Recargar config&f.");
			msg.add("&e    parse &f<&eformatMsg&f>\n&a  Procesa en tiempo real formatMsg&f(Sirve para testear ;D&f)&f.");

			for (int i = 0; i < msg.size(); i++) {
				Player p                   = (Player) sender;
				String[] l                 = msg.get(i).split("\n");
			    TextComponent message      = new TextComponent(l[0]);
			    ComponentBuilder hoverText = new ComponentBuilder("");

//			    message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/comando")); // Habria q hacer parentes de comandos, mucho lio...
			    for (int i2 = 1; i2 < l.length; i2++) {
			        hoverText.append(api.formatMsg(null, p, "", l[i2], "es", api.getLang(p)));
			        if (i2 < l.length - 1) {
			            hoverText.append("\n");
			        }
			    }
			    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create()));
			    p.spigot().sendMessage(message);
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
