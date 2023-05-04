package majhrs16.ct.commands;

import java.util.ArrayList;
import java.util.UUID;
import majhrs16.ct.GT;
import majhrs16.ct.main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
  
  public class _Sender {
    private CommandSender sender;
    
    public _Sender(CommandSender sender) {
      this.sender = sender;
    }
    
    public boolean isConsole() {
      return this.sender instanceof org.bukkit.craftbukkit.v1_8_R3.command.ColouredConsoleSender;
    }
    
    public boolean isPlayer() {
      return this.sender instanceof Player;
    }
    
    public void sendMessage(String s) {
      this.sender.sendMessage(s);
    }
    
    public Location getLocation() {
      if (isPlayer())
        return ((Player)this.sender).getLocation(); 
      return null;
    }
    
    public GameMode getGameMode() {
      if (isPlayer())
        return ((Player)this.sender).getGameMode(); 
      return null;
    }
    
    public String getName() {
      return this.sender.getName();
    }
    
    public boolean hasPermission(String s) {
      return this.sender.hasPermission(s);
    }
    
    public UUID getUniqueId() {
      if (isPlayer())
        return ((Player)this.sender).getUniqueId(); 
      return null;
    }
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    FileConfiguration config = this.plugin.getConfig();
    GT g = new GT();
    _Sender player = new _Sender(sender);
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
    ArrayList<String> msg = new ArrayList<>();
    msg.add(String.valueOf(this.plugin.name) + ChatColor.translateAlternateColorCodes("&".charAt(0), " &e/ct"));
    msg.add(String.valueOf(this.plugin.name) + ChatColor.translateAlternateColorCodes("&".charAt(0), " &e  version &a%msg%&f.".replace("%msg%", g.translateText("Ver version.", "es", config.getString("default-lang")))));
    msg.add(String.valueOf(this.plugin.name) + ChatColor.translateAlternateColorCodes("&".charAt(0), " &e  reload &a%msg%&f.".replace("%msg%", g.translateText("Recargar config.", "es", config.getString("default-lang")))));
    for (int i = 0; i < msg.size(); i++)
      player.sendMessage(msg.get(i)); 
    return true;
  }
}
