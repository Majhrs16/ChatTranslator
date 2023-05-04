package majhrs16.ct.commands;

import majhrs16.ct.GT;
import majhrs16.ct.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class mainCommand implements CommandExecutor {
  private main plugin;
  
  public mainCommand(main plugin) {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    GT g = new GT();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (args.length > 0) {
        if (args[0].equalsIgnoreCase("version")) {
          player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " " + g.translateText("Version:", "es", config.getString("default-lang")) + " " + ChatColor.RED + this.plugin.version);
          return true;
        } 
        if (args[0].equalsIgnoreCase("reload")) {
          player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.YELLOW + " " + g.translateText("Recargando configuracion...", "es", config.getString("default-lang")));
          this.plugin.reloadConfig();
          player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.GREEN + " " + g.translateText("La config ha sido recargada correctamente.", "es", config.getString("default-lang")));
          return true;
        } 
        player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " " + g.translateText("Ese comando no existe!", "es", config.getString("default-lang")));
        return false;
      } 
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " /ct");
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   version" + ChatColor.GREEN + " " + g.translateText("Ver version.", "es", config.getString("default-lang")));
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   reload" + ChatColor.GREEN + " " + g.translateText("Recargar config.", "es", config.getString("default-lang")));
      return true;
    } 
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("version")) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " " + g.translateText("Version:", "es", config.getString("default-lang")) + " " + ChatColor.RED + this.plugin.version);
        return true;
      } 
      if (args[0].equalsIgnoreCase("reload")) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.YELLOW + " " + g.translateText("Recargando configuracion...", "es", config.getString("default-lang")));
        this.plugin.reloadConfig();
        Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.GREEN + " " + g.translateText("La config ha sido recargada correctamente.", "es", config.getString("default-lang")));
        return true;
      } 
      Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " " + g.translateText("Ese comando no existe!", "es", config.getString("default-lang")));
      return false;
    } 
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " /ct");
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   version" + ChatColor.GREEN + " " + g.translateText("Ver version.", "es", config.getString("default-lang")));
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   reload" + ChatColor.GREEN + " " + g.translateText("Recargar config.", "es", config.getString("default-lang")));
    return true;
  }
}
