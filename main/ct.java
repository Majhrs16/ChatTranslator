package main;

import commands.mainCommand;
import commands.setLang;
import events.Chat;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ct extends JavaPlugin {
  public String rutaConfig;
  
  PluginDescriptionFile pdffile = getDescription();
  
  public String version = this.pdffile.getVersion();
  
  public String name = ChatColor.YELLOW + "[" + ChatColor.GREEN + this.pdffile.getName() + ChatColor.YELLOW + "]";
  
  public void onEnable() {
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "<------------------------->");
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.name) + ChatColor.WHITE + " activado. (" + ChatColor.RED + this.version + ChatColor.WHITE + ")");
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "<------------------------->");
    RegistryCommands();
    RegistryEvents();
    RegistryConfig();
  }
  
  public void onDisable() {
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "<------------------------->");
    Bukkit.getConsoleSender().sendMessage(String.valueOf(this.name) + ChatColor.WHITE + " desactivado. (" + ChatColor.RED + this.version + ChatColor.WHITE + ")");
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "<------------------------->");
  }
  
  public void RegistryCommands() {
    getCommand("ChatTranslator").setExecutor((CommandExecutor)new mainCommand(this));
    getCommand("ct").setExecutor((CommandExecutor)new mainCommand(this));
    getCommand("lang").setExecutor((CommandExecutor)new setLang(this));
  }
  
  public void RegistryEvents() {
    PluginManager pe = getServer().getPluginManager();
    pe.registerEvents((Listener)new Chat(this), (Plugin)this);
  }
  
  public void RegistryConfig() {
    File config = new File(getDataFolder(), "config.yml");
    this.rutaConfig = config.getPath();
    if (!config.exists()) {
      getConfig().options().copyDefaults(true);
      saveConfig();
    } 
  }
}
