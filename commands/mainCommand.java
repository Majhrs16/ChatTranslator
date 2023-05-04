package commands;

import main.ct;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mainCommand implements CommandExecutor {
  private ct plugin;
  
  public mainCommand(ct plugin) {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (args.length > 0) {
        if (args[0].equalsIgnoreCase("version")) {
          player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " Version: " + ChatColor.RED + this.plugin.version);
          return true;
        } 
        if (args[0].equalsIgnoreCase("reload")) {
          player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.YELLOW + " Recargando config...");
          this.plugin.reloadConfig();
          player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.GREEN + " La config ha sido recargada correctamente.");
          return true;
        } 
        player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " ese comando no existe!");
        return false;
      } 
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " /ct");
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   version" + ChatColor.GREEN + " ver version.");
      player.sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   reload" + ChatColor.GREEN + " Recargar config.");
      return true;
    } 
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("version")) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " Version: " + ChatColor.RED + this.plugin.version);
        return true;
      } 
      if (args[0].equalsIgnoreCase("reload")) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.YELLOW + " Recargando config...");
        this.plugin.reloadConfig();
        Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.GREEN + " La config ha sido recargada correctamente.");
        return true;
      } 
      Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.RED + " ese comando no existe!");
      return false;
    } 
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + " /ct");
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   version" + ChatColor.GREEN + " ver version.");
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.plugin.name) + ChatColor.WHITE + "   reload" + ChatColor.GREEN + " Recargar config.");
    return true;
  }
}
