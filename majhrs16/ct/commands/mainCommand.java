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
    String lang = this.api.getLang(sender);
    if (args.length > 0) {
      String str;
      switch ((str = args[0].toLowerCase()).hashCode()) {
        case -934641255:
          if (!str.equals("reload"))
            break; 
          if (!sender.hasPermission("ChatTranslator.admin")) {
            this.api.sendMessage(null, sender, "", "&cUsted no tiene permisos para ejecutar este comando&f.", "es");
            return false;
          } 
          updateConfig(sender, lang);
          return true;
        case 351608024:
          if (!str.equals("version"))
            break; 
          showVersion(sender, lang);
          return true;
      } 
      this.api.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&cEse comando no existe&f!", lang);
      return false;
    } 
    ArrayList<String> msg = new ArrayList<>();
    msg.add("&e/ct");
    msg.add("&e  version &a%msg%&f.".replace("%msg%", "Ver version."));
    msg.add("&e  reload &a%msg%&f.".replace("%msg%", "Recargar config."));
    for (int i = 0; i < msg.size(); i++)
      this.api.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " " + (String)msg.get(i), lang); 
    return true;
  }
  
  public void showVersion(CommandSender sender, String lang) {
    this.api.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + " &7Version&f: &a" + this.plugin.version, lang);
  }
  
  public void updateConfig(CommandSender sender, String lang) {
    this.api.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&7Recargando configuracion&f...", lang);
    this.plugin.reloadConfig();
    this.api.sendMessage(null, sender, "", String.valueOf(this.plugin.name) + "&7Recargando configuracion&f... &aOK&f.", lang);
  }
}
