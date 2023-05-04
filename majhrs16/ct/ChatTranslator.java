package majhrs16.ct;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import majhrs16.ct.commands.CT;
import majhrs16.ct.commands.Lang;
import majhrs16.ct.events.Chat;
import majhrs16.ct.translator.API;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatTranslator extends JavaPlugin {
  public String rutaConfig;
  
  private FileConfiguration players = null;
  
  private File playersFile = null;
  
  PluginDescriptionFile pdffile = getDescription();
  
  public String version = this.pdffile.getVersion();
  
  public String name = ChatColor.YELLOW + "[" + ChatColor.GREEN + this.pdffile.getName() + ChatColor.YELLOW + "]";
  
  public void onEnable() {
    RegistryConfig();
    RegisterPlayers();
    FileConfiguration config = getConfig();
    String path = "server-uuid";
    if (!config.contains(path)) {
      config.set(path, UUID.randomUUID());
      saveConfig();
    } 
    RegistryCommands();
    RegistryEvents();
    chatManager();
    API API = new API(this);
    ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
    try {
      Runtime.getRuntime().exec("chcp 65001 > nul");
      System.out.println("\r\n╔═╦╗   ╔╗╔══╗        ╔╗  ╔╗\r\n║╔╣╚╦═╦╣╠╬╣╠╬═╦═╦═╦══╣╠═╦╣╠╦═╦═╗\r\n║╚╣║╠╝╠╗╔╣║║║╠╬╝║║╠╗╚╣╠╝╠╗╔╣║║╠╝\r\n╚═╩╩╩═╝╚═╝╚╝╚╝╚═╩╩╩══╩╩═╝╚═╩═╩╝");
    } catch (IOException e) {
      API.sendMessage(null, (CommandSender)consoleCommandSender, "", this.name, "en");
    } 
    API.sendMessage(null, (CommandSender)consoleCommandSender, "", "    &aActivado&f. &7Version&f: &av%version%&f.".replace("%version%", this.version), "es");
    if (!util.checkPAPI().booleanValue()) {
      API.sendMessage(null, (CommandSender)consoleCommandSender, "", " ", "es");
      API.sendMessage(null, (CommandSender)consoleCommandSender, "", "&c    No esta disponible PlaceHolderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas de &a" + this.pdffile.getName(), "es");
    } 
    updateChecker();
  }
  
  public void onDisable() {
    API API = new API(this);
    ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
    API.sendMessage(null, (CommandSender)consoleCommandSender, "", "&4<------------------------->", "es");
    API.sendMessage(null, (CommandSender)consoleCommandSender, "", String.valueOf(this.name) + "&cDesactivado&f.", "es");
    API.sendMessage(null, (CommandSender)consoleCommandSender, "", "&4<------------------------->", "es");
  }
  
  public void chatManager() {
    final Chat Chat = new Chat(this);
    Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable() {
          public void run() {
            for (int i = 0; i < util.chat.size(); i += 30) {
              int end = Math.min(i + 30, util.chat.size());
              for (AsyncPlayerChatEvent event : util.chat.subList(i, end))
                Chat.processMsg(event); 
              util.chat.subList(i, end).clear();
            } 
          }
        }0L, 1L);
  }
  
  public void RegistryCommands() {
    getCommand("ChatTranslator").setExecutor((CommandExecutor)new CT(this));
    getCommand("ct").setExecutor((CommandExecutor)new CT(this));
    getCommand("lang").setExecutor((CommandExecutor)new Lang(this));
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
  
  public void updateChecker() {
    API API = new API(this);
    ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
    API.sendMessage(null, (CommandSender)consoleCommandSender, "", " ", "es");
    try {
      HttpURLConnection con = (HttpURLConnection)(new URL("https://API.spigotmc.org/legacy/update.php?resource=106604")).openConnection();
      int timed_out = 1250;
      con.setConnectTimeout(timed_out);
      con.setReadTimeout(timed_out);
      String latestversion = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
      if (latestversion.length() <= 7)
        if (!this.version.equals(latestversion)) {
          API.sendMessage(null, (CommandSender)consoleCommandSender, "", String.valueOf(this.name) + "&e    Hay una nueva version disponible&f! &f(&a%latestversion%&f)".replace("%latestversion%", latestversion), "es");
          API.sendMessage(null, (CommandSender)consoleCommandSender, "", String.valueOf(this.name) + "&a        Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/", "es");
        } else {
          API.sendMessage(null, (CommandSender)consoleCommandSender, "", String.valueOf(this.name) + "    &a  Estas usando la ultima version del plugin <3", "es");
        }  
    } catch (Exception ex) {
      API.sendMessage(null, (CommandSender)consoleCommandSender, "", String.valueOf(this.name) + "&c    Error mientras se buscaban actualizaciones&f.", "es");
    } 
  }
  
  public FileConfiguration getPlayers() {
    if (this.players == null)
      reloadPlayers(); 
    return this.players;
  }
  
  public void reloadPlayers() {
    if (this.players == null)
      this.playersFile = new File(getDataFolder(), "players.yml"); 
    this.players = (FileConfiguration)YamlConfiguration.loadConfiguration(this.playersFile);
    try {
      Reader defConfigStream = new InputStreamReader(getResource("players.yml"), "UTF8");
      if (defConfigStream != null) {
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        this.players.setDefaults((Configuration)defConfig);
      } 
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } 
  }
  
  public void savePlayers() {
    try {
      this.players.save(this.playersFile);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public void RegisterPlayers() {
    this.playersFile = new File(getDataFolder(), "players.yml");
    if (!this.playersFile.exists()) {
      getPlayers().options().copyDefaults(true);
      savePlayers();
    } 
  }
}
