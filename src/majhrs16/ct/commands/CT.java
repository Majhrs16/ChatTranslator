package majhrs16.ct.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import majhrs16.ct.API;
import majhrs16.ct.Main;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CT implements CommandExecutor {
	private Main plugin;
	private API API;
	public CT(Main plugin) {
		this.plugin = plugin;
		this.API    = new API(plugin);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String lang = API.getLang(sender);

		if(args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "version":
					showVersion(sender, lang);
					return true;
	
				case "reload":
					if (!sender.hasPermission("ChatTranslator.admin")) {
						API.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
						return false;
					}

					updateConfig(sender, lang);
					return true;
	
				default:
					API.sendMessage(null, sender, "", plugin.name + "&7Ese comando &cno existe&f!", lang);
					return false;
			}

		} else {
			ArrayList<String> msg = new ArrayList<String>();
			msg.add(plugin.name);
			msg.add("&e  /lang <lang>\n&7Especifique con su codigo de idioma&f, &apara traducir el chat a su gusto&f.\n&f  (&7Independientemente de su lenguaje en el Minecraft&f).");
			msg.add("");
			msg.add("&e  /ct");
			msg.add("&e    version\n&aVisualizar version&f.");
			msg.add("&e    reload\n&aRecargar config&f.");
			msg.add("&e    parse &f<&eformatMsg&f>\n&aProcesa en tiempo real formatMsg&f(&7Sirve para testear &f;&aD&f)&f.\n&e  EN DESARROLLO&f.");

			for (int i = 0; i < msg.size(); i++) {
				if (msg.get(i) == "")
					sender.sendMessage("");

				String[] l         = msg.get(i).split("\n", 2);
				String title       = ChatColor.translateAlternateColorCodes("&".charAt(0), l[0]);
				String description = "";

				if (l.length > 1)
					description = API.formatMsg(sender, "", l[1], "es", API.getLang(sender));

				if (sender instanceof Player) {
					Player p                   = (Player) sender;
				    TextComponent message      = new TextComponent(title);
				    ComponentBuilder hoverText = new ComponentBuilder(description);

				    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create()));
				    /* message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/comando")); // Habria q hacer parentes de comandos, mucho lio... */
				    p.spigot().sendMessage(message);

				} else {
					Bukkit.getConsoleSender().sendMessage(title);
					if (!description.equals(""))
						Bukkit.getConsoleSender().sendMessage("\t" + description);
				}
			}

			return true;
		}
	}
	
	public void showVersion(CommandSender sender, String lang) {
		API.sendMessage(null, sender, "", plugin.name + " &7Version&f: &a" + plugin.version, lang); 
	}
	
	public void updateConfig(CommandSender sender, String lang) {
		try {
			plugin.reloadConfig();
			API.sendMessage(null, sender, "", plugin.name + "&7Recargado config.yml&f.", lang);
			plugin.reloadPlayers();
			API.sendMessage(null, sender, "", plugin.name + "&7Recargado players.yml&f.", lang);
			API.sendMessage(null, sender, "", plugin.name + "&7Config recargada &aexitosamente&f.", lang);

		} catch (Exception e) {
			API.sendMessage(null, sender, "", plugin.name + "&f[&4ERROR&f] &cNO se pudo recargar la config&f. &ePor favor, vea su consola &f/ &eterminal&f.", lang);
			e.printStackTrace();
		}
	}
}
