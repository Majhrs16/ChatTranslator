package majhrs16.ct;

import java.util.List;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class api {
  private main plugin;
  
  public api(main plugin) {
    this.plugin = plugin;
  }
  
  public void broadcast(_Sender player, String msg, String sourceLang) {
    List<Player> players = (List<Player>)Bukkit.getOnlinePlayers();
    sendMessage(new _Sender((CommandSender)Bukkit.getConsoleSender()), msg, sourceLang);
    for (int i = 0; i < players.size(); i++)
      sendMessage(new _Sender((CommandSender)players.get(i)), msg, sourceLang); 
  }
  
  public void sendMessage(_Sender player, String msg, String sourceLang) {
    String targetLang = null;
    FileConfiguration config = this.plugin.getConfig();
    if (player.isPlayer()) {
      targetLang = getLang(player);
    } else if (player.isConsole()) {
      targetLang = config.getString("default-lang");
    } 
    if (IF(config, "auto-format-chat"))
      msg = PlaceholderAPI.setPlaceholders(player.getPlayer(), msg); 
    if (IF(config, "auto-translate-chat")) {
      Integer i = Integer.valueOf(0);
      if (msg.startsWith(this.plugin.name))
        i = Integer.valueOf(this.plugin.name.length()); 
      msg = (new GT()).translateText(msg.substring(i.intValue(), msg.length()), sourceLang, targetLang);
      if (i.intValue() > 0)
        msg = String.valueOf(this.plugin.name) + msg; 
    } 
    if (IF(config, "message-color-personalized"))
      msg = ChatColor.translateAlternateColorCodes("&".charAt(0), msg); 
    player.sendMessage(msg);
  }
  
  public String getLang(_Sender player) {
    String lang = null;
    FileConfiguration config = this.plugin.getConfig();
    String defaultLang = config.getString("default-lang");
    String path = "players.";
    GT g = new GT();
    if (config.contains(String.valueOf(path) + player.getUniqueId())) {
      lang = config.getString(String.valueOf(path) + player.getUniqueId());
    } else if (player.isPlayer()) {
      lang = PlaceholderAPI.setPlaceholders(player.getPlayer(), "%player_locale_short%");
    } else if (player.isConsole()) {
      lang = defaultLang;
    } 
    if (!g.isSupport(lang))
      if (g.isSupport(defaultLang)) {
        sendMessage(player, String.valueOf(this.plugin.name) + " &7El idioma &f'&b" + lang + "&f' &7no esta soportado&f.", "es");
        lang = defaultLang;
      } else {
        sendMessage(player, String.valueOf(this.plugin.name) + "&7El idioma por defecto &f'&b" + defaultLang + "&f' &7no esta soportado&f!.", "es");
        return null;
      }  
    return lang;
  }
  
  public boolean IF(FileConfiguration cfg, String path) {
    return (cfg.contains(path) && cfg.getString(path).equals("true"));
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
        return getPlayer().getLocation(); 
      return null;
    }
    
    public GameMode getGameMode() {
      if (isPlayer())
        return getPlayer().getGameMode(); 
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
        return getPlayer().getUniqueId(); 
      return null;
    }
    
    public Player getPlayer() {
      if (isPlayer())
        return (Player)this.sender; 
      if (isConsole())
        return null; 
      return null;
    }
    
    public CommandSender getConsole() {
      if (isConsole())
        return this.sender; 
      if (isPlayer())
        return null; 
      return null;
    }
  }
}
