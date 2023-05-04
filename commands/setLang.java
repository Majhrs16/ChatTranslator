package commands;

import main.GT;
import main.ct;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class setLang implements CommandExecutor {
  private ct plugin;
  
  public setLang(ct plugin) {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (args.length > 0) {
        GT g = new GT();
        String lang = g.getCode(args[0]);
        if (g.isSupport(lang)) {
          FileConfiguration config = this.plugin.getConfig();
          config.set("players." + player.getUniqueId(), lang);
          this.plugin.saveConfig();
          String msg = ChatColor.GREEN + "Su idioma ha sido establecido a " + lang;
          try {
            msg = g.translateText(msg, "es", lang);
          } catch (Exception e) {
            e.printStackTrace();
          } 
          player.sendMessage(String.valueOf(this.plugin.name) + " " + msg);
          return true;
        } 
        player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " Por favor use un codigo de lenguaje valido.");
        return false;
      } 
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " Por favor use un codigo de lenguaje valido.");
      return false;
    } 
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " No comandos desde consola.");
    return false;
  }
}
