package majhrs16.ct.events;

import java.util.List;
import majhrs16.ct.GT;
import majhrs16.ct.main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class chat implements Listener {
  private main plugin;
  
  public chat(main plugin) {
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
    String prefix = config.getString("message-prefix").replace("{lang}", sourceLang).replace("{nick}", event.getPlayer().getName());
    for (int i = 0; i < players.size(); i++) {
      if (((Player)players.get(i)).getUniqueId() != event.getPlayer().getUniqueId()) {
        String targetLang;
        if (config.contains(String.valueOf(path) + ((Player)players.get(i)).getUniqueId())) {
          targetLang = config.getString(String.valueOf(path) + ((Player)players.get(i)).getUniqueId());
        } else {
          targetLang = config.getString("default-lang");
        } 
        if (this.plugin.IF(config, "auto-translate-chat"))
          msg = g.translateText(msg, sourceLang, targetLang); 
        ((Player)players.get(i)).sendMessage(String.valueOf(prefix) + msg);
      } else {
        event.getPlayer().sendMessage(String.valueOf(prefix) + msg);
      } 
      Bukkit.getConsoleSender().sendMessage(String.valueOf(prefix) + msg);
    } 
  }
}
