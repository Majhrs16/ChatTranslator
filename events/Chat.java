package events;

import java.util.List;
import main.GT;
import main.ct;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {
  private ct plugin;
  
  public Chat(ct plugin) {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onMessage(AsyncPlayerChatEvent event) throws Exception {
    String sourceLang;
    FileConfiguration config = this.plugin.getConfig();
    List<Player> players = (List<Player>)Bukkit.getOnlinePlayers();
    GT g = new GT();
    String msg = event.getMessage();
    String path = "players.";
    event.setCancelled(true);
    if (config.contains(String.valueOf(path) + event.getPlayer().getUniqueId())) {
      sourceLang = config.getString(String.valueOf(path) + event.getPlayer().getUniqueId());
    } else {
      sourceLang = config.getString("default-lang");
    } 
    String prefix = config.getString("message-prefix");
    prefix = prefix.replace("{lang}", sourceLang);
    prefix = prefix.replace("{nick}", event.getPlayer().getName());
    for (int i = 0; i < players.size(); i++) {
      if (((Player)players.get(i)).getUniqueId() != event.getPlayer().getUniqueId()) {
        String targetLang, msg2;
        if (config.contains(String.valueOf(path) + ((Player)players.get(i)).getUniqueId())) {
          targetLang = config.getString(String.valueOf(path) + ((Player)players.get(i)).getUniqueId());
        } else {
          targetLang = config.getString("default-lang");
        } 
        try {
          msg2 = g.translateText(msg, sourceLang, targetLang);
        } catch (Exception e) {
          e.printStackTrace();
          msg2 = msg;
        } 
        ((Player)players.get(i)).sendMessage(String.valueOf(prefix) + msg2);
      } else {
        event.getPlayer().sendMessage(String.valueOf(prefix) + msg);
      } 
    } 
  }
}
