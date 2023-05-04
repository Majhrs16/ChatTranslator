package majhrs16.ct.events;

import majhrs16.ct.api;
import majhrs16.ct.main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class chat implements Listener {
  private main plugin;
  
  private api api;
  
  public chat(main plugin) {
    this.plugin = plugin;
    this.api = new api(plugin);
  }
  
  @EventHandler
  public void onMessage(AsyncPlayerChatEvent event) throws Exception {
    event.setCancelled(true);
    FileConfiguration config = this.plugin.getConfig();
    String msgFormat = config.getString("message-format");
    this.api.getClass();
    api._Sender player = new api._Sender(this.api, (CommandSender)event.getPlayer());
    msgFormat = msgFormat.replace("%player%", player.getName());
    msgFormat = msgFormat.replace("%lang%", this.api.getLang(player));
    this.api.getClass();
    this.api.broadcast(new api._Sender(this.api, (CommandSender)event.getPlayer()), msgFormat.replace("%msg%", event.getMessage()), this.api.getLang(player));
  }
}
