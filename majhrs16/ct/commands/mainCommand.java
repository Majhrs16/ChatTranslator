package majhrs16.ct.commands;

import java.util.ArrayList;
import majhrs16.ct.api;
import majhrs16.ct.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class mainCommand implements CommandExecutor {
  private main plugin;
  
  private api api;
  
  public mainCommand(main plugin) {
    this.plugin = plugin;
    this.api = new api(plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    this.api.getClass();
    api._Sender player = new api._Sender(this.api, sender);
    String lang = this.api.getLang(player);
    if (args.length > 0) {
      String str;
      switch ((str = args[0].toLowerCase()).hashCode()) {
        case -934641255:
          if (!str.equals("reload"))
            break; 
          this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + "&7Recargando configuracion&f...", lang);
          this.plugin.reloadConfig();
          this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + "&7Recargando configuracion&f... &aOK&f.", lang);
          return true;
        case 351608024:
          if (!str.equals("version"))
            break; 
          this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + " &7Version&f: &a" + this.plugin.version, lang);
          return true;
      } 
      this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + "&cEse comando no existe&f!", lang);
      return false;
    } 
    ArrayList<String> msg = new ArrayList<>();
    msg.add("&e/ct");
    msg.add("&e  version &a%msg%&f.".replace("%msg%", "Ver version."));
    msg.add("&e  reload &a%msg%&f.".replace("%msg%", "Recargar config."));
    for (int i = 0; i < msg.size(); i++)
      this.api.sendMessage(player, "", String.valueOf(this.plugin.name) + " " + (String)msg.get(i), lang); 
    return true;
  }
}
