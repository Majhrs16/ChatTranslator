package majhrs16.ct;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import majhrs16.ct.commands.mainCommand;
import majhrs16.ct.commands.setLang;
import majhrs16.ct.events.chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
  public String rutaConfig;
  
  private FileConfiguration players = null;
  
  private File playersFile = null;
  
  PluginDescriptionFile pdffile = getDescription();
  
  public String version = this.pdffile.getVersion();
  
  public String name = ChatColor.YELLOW + "[" + ChatColor.GREEN + this.pdffile.getName() + ChatColor.YELLOW + "]";
  
  public void onEnable() {
    api api = new api(this);
    api.getClass();
    api._Sender console = new api._Sender(api, (CommandSender)Bukkit.getConsoleSender());
    api.sendMessage(console, "", "&4<------------------------->", "es");
    api.sendMessage(console, "", String.valueOf(this.name) + "&aActivado&f. &f(&a%version%&f)".replace("%version%", this.version), "es");
    api.sendMessage(console, "", " ", "es");
    updateChecker();
    api.sendMessage(console, "", "&4<------------------------->", "es");
    RegistryConfig();
    RegistryCommands();
    RegistryEvents();
    if (checkPAPI().booleanValue())
      api.sendMessage(console, "", "&cNo esta disponible PlaceHolderAPI&f, &ePor favor instalarlo para disfrutar de todas las caracteristicas de &a" + this.pdffile.getName(), "es"); 
  }
  
  public Boolean checkPAPI() {
    Boolean havePAPI = null;
    try {
      Class.forName("me.clip.placeholderapi.PlaceholderAPI");
      havePAPI = Boolean.valueOf(true);
    } catch (ClassNotFoundException e) {
      havePAPI = Boolean.valueOf(false);
    } 
    return havePAPI;
  }
  
  public void onDisable() {
    api api = new api(this);
    api.getClass();
    api._Sender console = new api._Sender(api, (CommandSender)Bukkit.getConsoleSender());
    api.sendMessage(console, "", "&4<------------------------->", "es");
    api.sendMessage(console, "", String.valueOf(this.name) + "&cDesactivado&f.", "es");
    api.sendMessage(console, "", "&4<------------------------->", "es");
  }
  
  public void RegistryCommands() {
    getCommand("ChatTranslator").setExecutor((CommandExecutor)new mainCommand(this));
    getCommand("ct").setExecutor((CommandExecutor)new mainCommand(this));
    getCommand("lang").setExecutor((CommandExecutor)new setLang(this));
  }
  
  public void RegistryEvents() {
    PluginManager pe = getServer().getPluginManager();
    pe.registerEvents((Listener)new chat(this), (Plugin)this);
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
    api api = new api(this);
    api.getClass();
    api._Sender console = new api._Sender(api, (CommandSender)Bukkit.getConsoleSender());
    try {
      HttpURLConnection con = (HttpURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=106604")).openConnection();
      int timed_out = 1250;
      con.setConnectTimeout(timed_out);
      con.setReadTimeout(timed_out);
      String latestversion = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
      if (latestversion.length() <= 7 && 
        !this.version.equals(latestversion)) {
        api.sendMessage(console, "", String.valueOf(this.name) + "&e Hay una nueva version disponible&f! &f(&a%latestversion%&f)".replace("%latestversion%", latestversion), "es");
        api.sendMessage(console, "", String.valueOf(this.name) + "&a   Puedes descargarla en &9https://www.spigotmc.org/resources/chattranslator.106604/", "es");
      } 
    } catch (Exception ex) {
      api.sendMessage(console, "", String.valueOf(this.name) + "&c Error mientras se buscaban actualizaciones&f.", "es");
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
