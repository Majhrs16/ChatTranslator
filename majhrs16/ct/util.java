package majhrs16.ct;

import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class util {
  private main plugin;
  
  public static ArrayList<AsyncPlayerChatEvent> chat = new ArrayList<>();
  
  public util(main plugin) {
    this.plugin = plugin;
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
  
  public String getUUID(CommandSender sender) {
    String UUID = "";
    try {
      UUID = String.valueOf(UUID) + ((Player)sender).getUniqueId();
    } catch (ClassCastException e) {
      UUID = String.valueOf(UUID) + "0";
    } 
    return UUID;
  }
  
  public boolean IF(FileConfiguration cfg, String path) {
    return (cfg.contains(path) && cfg.getString(path).equals("true"));
  }
  
  public boolean IF(String path) {
    return IF(this.plugin.getConfig(), path);
  }
  
  public AsyncPlayerChatEvent popChat(int i) {
    AsyncPlayerChatEvent event;
    try {
      event = chat.get(i);
      chat.remove(i);
    } catch (IndexOutOfBoundsException e) {
      event = null;
    } 
    return event;
  }
}
